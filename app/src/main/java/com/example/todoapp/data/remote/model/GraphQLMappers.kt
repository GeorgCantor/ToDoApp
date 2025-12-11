package com.example.todoapp.data.remote.model

import com.example.todoapp.LaunchesQuery
import com.example.todoapp.domain.model.LaunchLinks
import com.example.todoapp.domain.model.LaunchSite
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
                siteName = launch_site?.site_name ?: "Unknown Site",
                siteNameLong = launch_site?.site_name_long,
            ),
        links =
            LaunchLinks(
                missionPatch = links?.mission_patch,
                missionPatchSmall = links?.mission_patch_small,
                articleLink = links?.article_link,
                videoLink = links?.video_link,
                wikipedia = links?.wikipedia,
                redditCampaign = links?.reddit_campaign,
            ),
        upcoming = upcoming ?: false,
    )
