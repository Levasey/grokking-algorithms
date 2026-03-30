package chapter13.httpsDh;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * Классический обмен ключами Диффи–Хеллмана в мультипликативной группе по модулю простого {@code p}
 * с образующим {@code g}: общий секрет {@code g^(ab) mod p}, где {@code a} и {@code b} — закрытые выборы сторон.
 */
public final class DiffieHellman {

    private DiffieHellman() {
    }

    /**
     * Публичные параметры группы (как в ServerKeyExchange для DHE).
     */
    public record Parameters(BigInteger modulus, BigInteger generator) {
        public Parameters {
            Objects.requireNonNull(modulus, "modulus");
            Objects.requireNonNull(generator, "generator");
            if (modulus.compareTo(BigInteger.valueOf(3)) < 0) {
                throw new IllegalArgumentException("modulus must be >= 3");
            }
            if (generator.signum() <= 0 || generator.compareTo(modulus.subtract(BigInteger.ONE)) >= 0) {
                throw new IllegalArgumentException("generator must be in [1, p-1]");
            }
        }
    }

    /**
     * Сторона протокола с закрытым ключом и вычисляемым открытым {@code g^secret mod p}.
     */
    public static final class Party {
        private final Parameters parameters;
        private final BigInteger privateKey;
        private final BigInteger publicKey;

        /**
         * Случайный закрытый ключ в диапазоне {@code [2, p-2]}.
         */
        public Party(Parameters parameters, SecureRandom random) {
            this.parameters = Objects.requireNonNull(parameters, "parameters");
            Objects.requireNonNull(random, "random");
            BigInteger p = parameters.modulus();
            BigInteger maxExclusive = p.subtract(BigInteger.TWO); // p-2
            this.privateKey = randomBigIntegerInRange(random, BigInteger.TWO, maxExclusive);
            this.publicKey = parameters.generator().modPow(privateKey, p);
        }

        /**
         * Фиксированный закрытый ключ (удобно для тестов и воспроизводимых примеров из учебников).
         */
        public Party(Parameters parameters, BigInteger privateKey) {
            this.parameters = Objects.requireNonNull(parameters, "parameters");
            Objects.requireNonNull(privateKey, "privateKey");
            BigInteger p = parameters.modulus();
            if (privateKey.compareTo(BigInteger.TWO) < 0 || privateKey.compareTo(p.subtract(BigInteger.TWO)) > 0) {
                throw new IllegalArgumentException("privateKey must be in [2, p-2]");
            }
            this.privateKey = privateKey;
            this.publicKey = parameters.generator().modPow(privateKey, p);
        }

        public Parameters parameters() {
            return parameters;
        }

        public BigInteger privateKey() {
            return privateKey;
        }

        public BigInteger publicKey() {
            return publicKey;
        }

        /**
         * Общий секрет: {@code peerPublic^privateKey mod p}.
         */
        public BigInteger sharedSecret(BigInteger peerPublic) {
            Objects.requireNonNull(peerPublic, "peerPublic");
            return peerPublic.modPow(privateKey, parameters.modulus());
        }
    }

    /**
     * Малые учебные {@code p=23}, {@code g=5} (как в типичных примерах к учебникам); не использовать в реальных системах.
     */
    public static Parameters textbookDemoParameters() {
        return new Parameters(BigInteger.valueOf(23), BigInteger.valueOf(5));
    }

    private static BigInteger randomBigIntegerInRange(SecureRandom random, BigInteger minInclusive, BigInteger maxInclusive) {
        BigInteger span = maxInclusive.subtract(minInclusive).add(BigInteger.ONE);
        BigInteger offset;
        do {
            offset = new BigInteger(span.bitLength(), random);
        } while (offset.compareTo(span) >= 0);
        return minInclusive.add(offset);
    }
}
