package chapter13.httpsDh;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.Objects;

/**
 * Упрощённая модель того, как HTTPS (TLS) согласует сессионные ключи: после аутентификации сервера (сертификат)
 * стороны обмениваются эфемерными открытыми значениями Диффи–Хеллмана, получают одинаковый pre-master secret
 * и из него выводят симметричный ключ (здесь — демонстрация через SHA-256, без полного PRF/HKDF TLS 1.2).
 */
public final class HttpsKeyAgreement {

    private HttpsKeyAgreement() {
    }

    /**
     * То, что клиент видит в начале DHE-рукопожатия: параметры группы и открытый ключ сервера.
     */
    public record ServerDhOffer(DiffieHellman.Parameters parameters, BigInteger serverPublicKey) {
        public ServerDhOffer {
            Objects.requireNonNull(parameters, "parameters");
            Objects.requireNonNull(serverPublicKey, "serverPublicKey");
        }
    }

    /**
     * Ответ клиента с его эфемерным открытым ключом.
     */
    public record ClientDhResponse(BigInteger clientPublicKey) {
        public ClientDhResponse {
            Objects.requireNonNull(clientPublicKey, "clientPublicKey");
        }
    }

    /**
     * Результат: одинаковые на обеих сторонах байты сессионного ключа (первые 32 байта SHA-256 от pre-master).
     */
    public record SessionKeys(byte[] clientToServerKey, byte[] serverToClientKey) {
        public SessionKeys {
            Objects.requireNonNull(clientToServerKey, "clientToServerKey");
            Objects.requireNonNull(serverToClientKey, "serverToClientKey");
        }
    }

    /**
     * Симулирует DHE-часть TLS: сервер объявляет (p, g) и свой ephemeral public; клиент отвечает своим ephemeral public;
     * обе стороны вычисляют {@code g^(ab) mod p} и из него — два направленных ключа (условная «разбивка» общего секрета).
     */
    public static SessionKeys performDheHandshake(SecureRandom random) {
        DiffieHellman.Parameters params = DiffieHellman.textbookDemoParameters();
        DiffieHellman.Party server = new DiffieHellman.Party(params, random);
        DiffieHellman.Party client = new DiffieHellman.Party(params, random);

        BigInteger preMasterClient = client.sharedSecret(server.publicKey());
        BigInteger preMasterServer = server.sharedSecret(client.publicKey());
        if (!preMasterClient.equals(preMasterServer)) {
            throw new IllegalStateException("pre-master mismatch");
        }

        return deriveSessionKeys(preMasterClient);
    }

    /**
     * Явная последовательность сообщений (как «запись» ServerHello+ServerKeyExchange и ClientKeyExchange).
     *
     * @param trace буфер для текстовой трассировки или {@code null}, если вывод не нужен
     */
    public static SessionKeys performDheHandshake(SecureRandom random, StringBuilder trace) {
        DiffieHellman.Parameters params = DiffieHellman.textbookDemoParameters();
        DiffieHellman.Party server = new DiffieHellman.Party(params, random);
        DiffieHellman.Party client = new DiffieHellman.Party(params, random);

        if (trace != null) {
            trace.append("Server DH: p=").append(params.modulus())
                    .append(", g=").append(params.generator())
                    .append(", Ys=").append(server.publicKey()).append('\n');
            trace.append("Client DH: Xc=").append(client.publicKey()).append('\n');
        }

        BigInteger preMaster = client.sharedSecret(server.publicKey());
        if (trace != null) {
            trace.append("Pre-master secret: ").append(preMaster).append('\n');
        }
        SessionKeys keys = deriveSessionKeys(preMaster);
        if (trace != null) {
            HexFormat hf = HexFormat.of();
            trace.append("Derived CTS key (hex): ").append(hf.formatHex(keys.clientToServerKey())).append('\n');
            trace.append("Derived STC key (hex): ").append(hf.formatHex(keys.serverToClientKey())).append('\n');
        }
        return keys;
    }

    /**
     * Демонстрационный KDF: SHA-256 от pre-master и два дополнительных хеша от помеченных конкатенаций.
     * Это <em>не</em> PRF из TLS 1.2 и не HKDF (TLS 1.3); ключи годятся только для иллюстрации потока рукопожатия.
     *
     * @param preMasterSecret общий секрет Диффи–Хеллмана (как BigInteger)
     */
    public static SessionKeys deriveSessionKeys(BigInteger preMasterSecret) {
        byte[] pmBytes = preMasterSecret.toByteArray();
        byte[] h;
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            h = sha256.digest(pmBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
        byte[] cts = sha256Again(concat(h, "client-to-server".getBytes(StandardCharsets.UTF_8)));
        byte[] stc = sha256Again(concat(h, "server-to-client".getBytes(StandardCharsets.UTF_8)));
        return new SessionKeys(cts, stc);
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] out = new byte[a.length + b.length];
        System.arraycopy(a, 0, out, 0, a.length);
        System.arraycopy(b, 0, out, a.length, b.length);
        return out;
    }

    private static byte[] sha256Again(byte[] data) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}
