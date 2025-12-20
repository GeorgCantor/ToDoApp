package com.example.todoapp.domain.model

data class SpaceXLaunch(
    val id: String?,
    val missionName: String?,
    val launchDateUtc: String?,
    val launchSuccess: Boolean?,
    val upcoming: Boolean?,
    val rocketId: String?,
    val rocketName: String,
    val launchSite: LaunchSite,
    val details: String?,
    val links: Links,
    val ships: List<Ship> = emptyList(),
    val launchDateLocal: String? = null,
    val staticFireDateUtc: String? = null,
    val missionId: List<String?>? = emptyList(),
    val launchYear: String? = null,
    val isTentative: Boolean? = false,
    val tentativeMaxPrecision: String? = null,
)

data class LaunchSite(
    val siteId: String,
    val siteName: String,
    val siteNameLong: String?,
)

data class Links(
    val missionPatch: String?,
    val missionPatchSmall: String?,
    val articleLink: String?,
    val wikipedia: String?,
    val videoLink: String?,
    val flickrImages: List<String?> = emptyList(),
    val redditCampaign: String? = null,
    val redditLaunch: String? = null,
    val redditRecovery: String? = null,
    val redditMedia: String? = null,
    val presskit: String? = null,
)

data class Ship(
    val id: String?,
    val name: String?,
    val type: String?,
    val yearBuilt: Int?,
    val homePort: String?,
    val image: String?,
)

data class CrewMember(
    val id: String?,
    val name: String?,
    val agency: String?,
    val image: String?,
    val wikipedia: String?,
    val status: String?,
)

data class Payload(
    val id: String?,
    val name: String?,
    val type: String?,
    val orbit: String?,
    val customers: List<String?>?,
    val massKg: Double?,
)

data class Capsule(
    val id: String,
    val type: String?,
    val status: String?,
    val serial: String?,
)

data class RocketDetail(
    val id: String?,
    val name: String?,
    val type: String?,
    val company: String?,
    val country: String?,
    val description: String?,
    val heightMeters: Double?,
    val diameterMeters: Double?,
    val massKg: Double?,
    val costPerLaunch: Int?,
    val successRatePct: Int?,
    val firstFlight: String?,
    val wikipedia: String?,
)

sealed class SpaceXUiState {
    object Loading : SpaceXUiState()

    data class Success(
        val launches: List<SpaceXLaunch>,
    ) : SpaceXUiState()

    data class Error(
        val message: String,
    ) : SpaceXUiState()
}
