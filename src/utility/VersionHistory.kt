package utility

/**
 * Stores release history for the mod.
 * Keep newest entry at the top.
 */
data class VersionInfo(
    val version: String,
    val description: String,
    val additions: List<String>
)

object VersionHistory {
    val entries: List<VersionInfo> = listOf(
        VersionInfo(
            version = "1.001",
            description = "First complete gameplay prototype release with full element-flow simulation, customizable crafter workflow, and in-game debugging UI.",
            additions = listOf(
                "Built core element system (Element/Elements/Phase) with mass-heat-temperature state model.",
                "Implemented customizable crafter tile grid (LATiles/LATile) including border area handling and per-tile state storage.",
                "Implemented flow pipeline (FlowProcessor + FlowData) with neighbor checks, flow direction decisions, mass/heat transfer counting, and result application.",
                "Added ElementState behaviors: auto-fill, copy/reset, mass/heat mutation, and temperature refresh logic.",
                "Added interactive InnerView: tile rendering, zoom/pan, mouse hit-test, and click-to-apply debug values.",
                "Added customization dialogs and pages (CustomizeDialog/DebugPanel/TileData) for viewing and editing runtime tile and flow data.",
                "Unified numeric presentation using CT.format with significant-digit rounding and engineering-style units (B/M/K/m/μ).",
                "Improved stability with index bounds checks and defensive guards for invalid tile selection and click operations.",
                "Completed desktop and Android packaging workflow in Gradle (jar/jarAndroid/deploy) for mod distribution."
            )
        ),
        VersionInfo(
            version = "1.000",
            description = "Initial baseline version.",
            additions = emptyList()
        )
    )

    val latest: VersionInfo
        get() = entries.first()

    fun find(version: String): VersionInfo? {
        return entries.firstOrNull { it.version == version }
    }
}
