package com.example.update.repository

import com.example.update.model.RemoteVersionConfig

/**
 * Scenarios for testing and administrator demonstration of update behavior
 * without requiring a server redeployment.
 */
enum class AdminSimulationScenario(
    val title: String,
    val description: String
) {
    UP_TO_DATE(
        title = "Production (Up to Date)",
        description = "Currently installed version 1.0.0 is the latest available release."
    ),
    OPTIONAL_UPDATE_AVAILABLE(
        title = "Optional Update Available (v1.1.0)",
        description = "A newer version 1.1.0 is available, but current version 1.0.0 is still supported. User can skip or update."
    ),
    MANDATORY_UPDATE_AVAILABLE(
        title = "Mandatory Update Required (v1.1.0)",
        description = "Minimum supported version is 1.1.0. Version 1.0.0 is blocked until updated."
    ),
    SIMULATE_NETWORK_FAILURE(
        title = "Simulate Network Failure",
        description = "Simulates network timeout/disconnection during version check or download."
    ),
    SIMULATE_SLOW_CONNECTION(
        title = "Simulate Slow Connection",
        description = "Simulates low-bandwidth download with realistic progress increments."
    )
}

/**
 * Helper to convert scenario to corresponding [RemoteVersionConfig].
 */
fun AdminSimulationScenario.toRemoteConfig(): RemoteVersionConfig {
    return when (this) {
        AdminSimulationScenario.UP_TO_DATE -> RemoteVersionConfig.defaultProduction()
        AdminSimulationScenario.OPTIONAL_UPDATE_AVAILABLE -> RemoteVersionConfig.nextRelease(isMandatory = false)
        AdminSimulationScenario.MANDATORY_UPDATE_AVAILABLE -> RemoteVersionConfig.nextRelease(isMandatory = true)
        AdminSimulationScenario.SIMULATE_NETWORK_FAILURE -> RemoteVersionConfig.nextRelease(isMandatory = true)
        AdminSimulationScenario.SIMULATE_SLOW_CONNECTION -> RemoteVersionConfig.nextRelease(isMandatory = false)
    }
}
