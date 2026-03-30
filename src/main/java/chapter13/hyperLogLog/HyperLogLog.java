package chapter13.hyperLogLog;

import java.util.Objects;

/**
 * HyperLogLog: вероятностная оценка числа <em>различных</em> элементов (кардинальности) с небольшой
 * фиксированной памятью (~{@code 2^precision} регистров). Повторные вставки одного и того же значения
 * не увеличивают оценку. Оценка смещена и имеет типичную относительную погрешность порядка
 * {@code ~1.04 / sqrt(m)}, где {@code m = 2^precision}.
 */
public final class HyperLogLog {

    private final int precision;
    private final int registerCount;
    private final byte[] registers;

    /**
     * @param precision число бит под индекс регистра ({@code m = 2^precision}); типичный диапазон 4–18
     */
    public HyperLogLog(int precision) {
        if (precision < 4 || precision > 18) {
            throw new IllegalArgumentException("precision must be in [4, 18]");
        }
        this.precision = precision;
        this.registerCount = 1 << precision;
        this.registers = new byte[registerCount];
    }

    public int precision() {
        return precision;
    }

    /** Число регистров {@code m = 2^precision}. */
    public int registerCount() {
        return registerCount;
    }

    /**
     * Учитывает элемент в оценке кардинальности.
     */
    public void add(CharSequence element) {
        Objects.requireNonNull(element, "element");
        long h = mixHash64(element);
        int j = (int) (h & (registerCount - 1));
        int rho = rho(h);
        int cur = registers[j] & 0xFF;
        if (rho > cur) {
            registers[j] = (byte) rho;
        }
    }

    /**
     * Оценка числа различных добавленных значений (округление до ближайшего {@code long}).
     */
    public long estimateCardinality() {
        int zeroRegisters = 0;
        double sumInversePow2 = 0.0;
        for (int i = 0; i < registerCount; i++) {
            int reg = registers[i] & 0xFF;
            if (reg == 0) {
                zeroRegisters++;
            }
            sumInversePow2 += Math.scalb(1.0, -reg);
        }

        if (zeroRegisters == registerCount) {
            return 0;
        }

        double alpha = alpha(registerCount);
        double rawEstimate = alpha * registerCount * registerCount / sumInversePow2;

        // Для малых оценок — linear counting по числу нулевых регистров (Flajolet et al.).
        if (rawEstimate <= 2.5 * registerCount && zeroRegisters > 0) {
            return Math.round(registerCount * Math.log((double) registerCount / zeroRegisters));
        }

        return Math.round(rawEstimate);
    }

    /**
     * Длина префикса из нулей в «хвосте» хеша (после {@code precision} бит индекса) плюс единица;
     * совпадает с позицией первой единицы слева в суффиксе длины {@code 64 - precision}.
     */
    private int rho(long hash) {
        long w = hash >>> precision;
        if (w == 0) {
            return 64 - precision + 1;
        }
        int lz = Long.numberOfLeadingZeros(w) - precision + 1;
        return Math.min(Math.max(lz, 1), 64);
    }

    private static double alpha(int m) {
        return switch (m) {
            case 16 -> 0.673;
            case 32 -> 0.697;
            case 64 -> 0.709;
            default -> 0.7213 / (1.0 + 1.079 / m);
        };
    }

    private static long mixHash64(CharSequence s) {
        long h = 0x9e3779b97f4a7c15L;
        for (int i = 0; i < s.length(); i++) {
            h = h * 31 + s.charAt(i);
            h ^= (h >>> 33);
            h *= 0xff51afd7ed558ccdL;
        }
        h ^= h >>> 33;
        h *= 0xc4ceb9fe1a85ec53L;
        h ^= h >>> 33;
        return h;
    }
}
