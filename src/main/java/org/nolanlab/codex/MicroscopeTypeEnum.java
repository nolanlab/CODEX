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


}
