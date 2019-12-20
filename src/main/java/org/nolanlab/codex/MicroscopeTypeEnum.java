package org.nolanlab.codex;

/**
 *
 * @author Vishal
 */
public enum MicroscopeTypeEnum {
    KEYENCE("Keyence BZ-X710"),
    ZEISS("Zeiss ZEN"),
    LEICA("Leica DMI8");

    private final String type;

    MicroscopeTypeEnum(String type) {
        this.type = type;
    }

    public boolean equalsType(String otherType) {
        return type.equals(otherType);
    }

    public String toString() {
        return type;
    }

    public static MicroscopeTypeEnum getMicroscopeFromValue(String type) {
        switch (type) {
            case "Keyence BZ-X710":
                return KEYENCE;
            case "Zeiss ZEN":
                return ZEISS;
            case "Leica DMI8":
                return LEICA;
            default:
                return KEYENCE;
        }
    }


}
