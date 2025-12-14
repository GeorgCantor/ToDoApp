package com.example.todoapp.data.remote.model

import com.example.todoapp.LaunchDetailQuery
import com.example.todoapp.LaunchesQuery
import com.example.todoapp.RocketQuery
import com.example.todoapp.domain.model.CrewMember
import com.example.todoapp.domain.model.LaunchSite
import com.example.todoapp.domain.model.Links
import com.example.todoapp.domain.model.Payload
import com.example.todoapp.domain.model.RocketDetail
import com.example.todoapp.domain.model.Ship
import com.example.todoapp.domain.model.SpaceXLaunch

fun LaunchesQuery.Launch.toDomain(): SpaceXLaunch =
    SpaceXLaunch(
        id = id.orEmpty(),
        missionName = mission_name ?: "Unknown Mission",
        launchDateUtc = launch_date_utc.orEmpty(),
        rocketName = rocket?.rocket_name ?: "Unknown Rocket",
        launchSuccess = launch_success,
        details = details.orEmpty(),
        launchSite =
            LaunchSite(
                siteId = launch_site?.site_name.hashCode().toString(),
                siteName = launch_site?.site_name ?: "Unknown Site",
                siteNameLong = launch_site?.site_name_long,
            ),
        links =
            Links(
                missionPatch = links?.mission_patch,
                missionPatchSmall = links?.mission_patch_small,
                articleLink = links?.article_link,
                videoLink = links?.video_link,
                wikipedia = links?.wikipedia,
                redditCampaign = links?.reddit_campaign,
                youtubeId = null,
            ),
        upcoming = upcoming ?: false,
        rocketId = rocket?.rocket_name.hashCode().toString(),
    )

fun LaunchDetailQuery.Launch.toLaunchDetail(): SpaceXLaunch {
    val rocketInfo = rocket?.rocket
    val rocketId = rocket?.rocket?.id ?: rocketInfo?.id

    return SpaceXLaunch(
        id = id,
        missionName = mission_name,
        launchDateUtc = launch_date_utc,
        launchSuccess = launch_success,
        upcoming = upcoming,
        rocketId = rocketId,
        rocketName = rocketInfo?.name ?: rocket?.rocket?.name ?: "Unknown",
        launchSite =
            LaunchSite(
                siteId = launch_site?.site_id.orEmpty(),
                siteName = launch_site?.site_name ?: "Unknown",
                siteNameLong = launch_site?.site_name_long,
            ),
        details = details,
        links =
            Links(
                missionPatch = links?.mission_patch,
                missionPatchSmall = links?.mission_patch_small,
                articleLink = links?.article_link,
                wikipedia = links?.wikipedia,
                videoLink = links?.video_link,
                youtubeId = links?.youtube_id,
                flickrImages = links?.flickr_images.orEmpty(),
                redditCampaign = links?.reddit_campaign,
                redditLaunch = links?.reddit_launch,
                redditRecovery = links?.reddit_recovery,
                redditMedia = links?.reddit_media,
                presskit = links?.presskit,
            ),
        ships =
            ships
                ?.mapNotNull { ship ->
                    ship?.let {
                        Ship(
                            id = it.id,
                            name = it.name,
                            type = it.type,
                            yearBuilt = it.year_built,
                            homePort = it.home_port,
                            image = it.image,
                        )
                    }
                }.orEmpty(),
        crew =
            crew
                ?.mapNotNull { member ->
                    member?.let {
                        CrewMember(
                            id = it.id,
                            name = it.name,
                            agency = it.agency,
                            image = it.image,
                            wikipedia = it.wikipedia,
                            status = it.status,
                        )
                    }
                }.orEmpty(),
        capsules = capsules?.mapNotNull { it?.id }.orEmpty(),
        payloads =
            payloads
                ?.mapNotNull { payload ->
                    payload?.let {
                        Payload(
                            id = it.id,
                            name = it.name,
                            type = it.type,
                            orbit = it.orbit,
                            customers = it.customers.orEmpty(),
                            massKg = it.mass_kg,
                        )
                    }
                }.orEmpty(),
        launchDateLocal = launch_date_local,
        staticFireDateUtc = static_fire_date_utc,
        launchWindow = launch_window,
        missionId = mission_id.orEmpty(),
        launchYear = launch_year,
        isTentative = is_tentative,
        tentativeMaxPrecision = tentative_max_precision,
    )
}

fun RocketQuery.Rocket.toRocketDetail(): RocketDetail =
    RocketDetail(
        id = id,
        name = name,
        type = type,
        company = company,
        country = country,
        description = description,
        heightMeters = height?.meters,
        diameterMeters = diameter?.meters,
        massKg = mass?.kg?.toDouble(),
        costPerLaunch = cost_per_launch,
        successRatePct = success_rate_pct,
        firstFlight = first_flight,
        wikipedia = wikipedia,
    )
