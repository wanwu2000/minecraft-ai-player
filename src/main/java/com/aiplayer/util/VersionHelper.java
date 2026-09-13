package com.aiplayer.util;

import com.aiplayer.AiPlayerMod;

/**
 * Minecraft 版本检测工具
 */
public class VersionHelper {
    private static Integer cachedVersion = null;

    /**
     * 获取当前 Minecraft 主版本号
     * @return 版本号（如 1.21 返回 2100，1.20 返回 2000）
     */
    public static int getMCVersion() {
        if (cachedVersion != null) return cachedVersion;

        try {
            String version = net.minecraft.client.MinecraftClient.getInstance().getSession().getVersion();
            if (version == null) {
                version = System.getProperty("minecraft.version", "1.21");
            }

            // 解析版本号
            int major = 1;
            int minor = 21;

            if (version.contains(".")) {
                String[] parts = version.split("\\.");
                try {
                    major = Integer.parseInt(parts[0]);
                    minor = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    // 如果是 26.1.2 格式
                    if (parts.length >= 2) {
                        try {
                            major = Integer.parseInt(parts[0]);
                            minor = Integer.parseInt(parts[1]);
                        } catch (NumberFormatException ex) {
                            // 使用默认值
                        }
                    }
                }
            }

            cachedVersion = major * 1000 + minor;
            AiPlayerMod.LOGGER.info("Detected Minecraft version: {}.{} (code: {})", major, minor, cachedVersion);
            return cachedVersion;
        } catch (Exception e) {
            AiPlayerMod.LOGGER.warn("Failed to detect Minecraft version, defaulting to 1.21", e);
            cachedVersion = 1021; // 1.21
            return 1021;
        }
    }

    /**
     * 检查是否为 1.21 或更高版本
     */
    public static boolean isVersion1_21OrHigher() {
        return getMCVersion() >= 1021;
    }

    /**
     * 检查是否为 1.20.x 或更低版本
     */
    public static boolean isVersionLowerThan1_21() {
        return getMCVersion() < 1021;
    }

    /**
     * 获取版本描述字符串
     */
    public static String getVersionString() {
        try {
            return net.minecraft.client.MinecraftClient.getInstance().getSession().getVersion();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
