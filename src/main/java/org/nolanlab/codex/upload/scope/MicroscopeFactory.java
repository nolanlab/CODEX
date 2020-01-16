package org.nolanlab.codex.upload.scope;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Vishal
 */
public class MicroscopeFactory {

    public static Microscope getMicroscope(String criteria) {
        if (StringUtils.containsIgnoreCase(MicroscopeTypeEnum.KEYENCE.toString(), criteria)) {
            return new Keyence();
        }
        else if (StringUtils.containsIgnoreCase(MicroscopeTypeEnum.ZEISS.toString(), criteria)) {
            return new Zeiss();
        }
        return null;
    }
}
