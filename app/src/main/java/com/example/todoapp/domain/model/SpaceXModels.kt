package com.example.todoapp.domain.model

data class SpaceXLaunch(
    val id: String,
    val missionName: String,
    val launchDateUtc: String,
    val rocketName: String,
    val launchSuccess: Boolean?,
    val details: String?,
    val launchSite: LaunchSite,
    val links: LaunchLinks,
    val upcoming: Boolean
)

data class LaunchSite(
    val siteName: String,
    val siteNameLong: String?
)

data class LaunchLinks(
    val missionPatch: String?,
    val missionPatchSmall: String?,
    val articleLink: String?,
    val videoLink: String?,
    val wikipedia: String?,
    val redditCampaign: String?
)

data class SpaceXRocket(
    val id: String,
    val name: String,
    val description: String,
    val height: RocketDimension?,
    val diameter: RocketDimension?,
    val mass: RocketMass?,
    val firstFlight: String?,
    val active: Boolean
)

data class RocketDimension(
    val meters: Float?,
    val feet: Float?
)

data class RocketMass(
    val kg: Int?,
    val lb: Int?
)

sealed class SpaceXUiState {
    object Loading : SpaceXUiState()
    data class Success(val launches: List<SpaceXLaunch>) : SpaceXUiState()
    data class Error(val message: String) : SpaceXUiState()
}