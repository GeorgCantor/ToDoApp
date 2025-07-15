package com.example.todoapp.data.repository

import com.example.todoapp.data.remote.api.NewsApiService
import com.example.todoapp.domain.model.NewsArticle
import com.example.todoapp.domain.repository.NewsRepository

class NewsRepositoryImpl(private val apiService: NewsApiService) : NewsRepository {
    override suspend fun getTopHeadlines(): List<NewsArticle> {
        return try {
            val response = apiService.getTopHeadlines()
            if (response.status == "ok") {
                response.articles.map { it.toNewsArticle() }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getMockHeadlines(): List<NewsArticle> {
        return listOf(
            NewsArticle(
                id = 1,
                title = "DOJ urges Supreme Court to turn away Epstein accomplice Ghislaine Maxwell's appeal",
                description = "The Department of Justice on Monday urged the Supreme Court to turn away an appeal from Ghislaine Maxwell, the former associate of sex offender Jeffrey Epstein, who is currently serving a 20-year prison sentence for conspiring with and aiding Epstein in his sexual abuse of underage girls.\n" +
                        "\n" +
                        "Maxwell, 63, had urged the court earlier this year to review her case, arguing that an unusual co-conspirator's clause in Epstein's 2007 non-prosecution agreement with federal prosecutors in Florida barred her subsequent prosecution in New York. A district court and a federal appeals court previously rejected that argument, and the DOJ today urged the high court to do the same.\n" +
                        "\n" +
                        "\"That contention is incorrect, and petitioner does not show that it would succeed in any court of appeals,\" wrote U.S. Solicitor General D. John Sauer.\n" +
                        "\n" +
                        "At the core of Maxwell's petition for SCOTUS review is her contention that the language of Epstein's non-prosecution agreement (NPA) specifically limited his protection to the Southern District of Florida, whereas the language of the co-conspirator clause should have been read to prohibit her prosecution in any federal district.\n" +
                        "\n" +
                        "The co-conspirator clause stated that if \"Epstein successfully fulfills all of the terms and conditions of this agreement, the United States also agrees that it will not institute any criminal charges against any potential co-conspirators of Epstein, including but not limited to\" four of Epstein's assistants. Maxwell was not among the four women named.",
                url = "",
                urlToImage = "https://i.abcnewsfe.com/a/62fa4b3b-cf37-41b4-a131-8227d8df7609/texas-flooding-ap-mf-20250713_1752423909229_hpMain_1x1.jpg?w=208",
                publishedAt = "2024-07-15T10:30:45Z"
            ),
            NewsArticle(
                id = 2,
                title = "Inflation report to arrive as Trump, Fed disagree over tariff risks",
                description = "Oil prices have dropped 15% since Trump took office, bringing down the price of auto gasoline. Even a blistering surge in egg prices has slowed. The price of eggs climbed 41% over the year ending in May, which marks a reduction from 53% inflation recorded in January.\n" +
                        "\n" +
                        "Speaking at the White House on Monday, Trump touted the reduction of inflation so far this year.\n" +
                        "\n" +
                        "\"The economy is roaring, business confidence is soaring, incomes are up, prices are down and inflation is dead,\" Trump said. \"It's dead.\"\n" +
                        "\n" +
                        "While inflation has eased, price increases have persisted at a higher rate than the Federal Reserve's target level of 2%.\n" +
                        "\n" +
                        "Some analysts expect price increases to accelerate over the coming months as tariffs take hold, though they acknowledged that the path forward remains unclear amid Trump's fluctuating policy.\n" +
                        "\n" +
                        "Typically, importers pass along a share of the tariff-related tax burden in the form of higher costs for shoppers. A host of major retailers, including Walmart and Best Buy, has warned about potential price hikes as a result of Trump's levies.",
                url = "",
                urlToImage = "https://i.abcnewsfe.com/a/c956d573-03e2-4f7d-a90c-e135a6d8dc0f/state-dept-02-gty-jef-250711_1752250402054_hpMain_1x1.jpg?w=208",
                publishedAt = "2024-07-15T10:30:45Z"
            ),
            NewsArticle(
                id = 3,
                title = "GHF unveils new 'flag system' at aid site in southern Gaza as Palestinians continue to report chaos, deaths",
                description = "GAZA and LONDON -- The U.S.-backed Gaza Humanitarian Foundation (GHF) announced some changes at its Khan Younis aid distribution center on Monday, as Palestinians continue to report mass killings and chaos near aid distribution sites in the Gaza Strip.\n" +
                        "\n" +
                        "The center will now use \"a flag system\" in place to indicate the status of the site, with the red flag signifying the site is closed and the green flag showing it is open, according to a social media post from GHF.\n" +
                        "\n" +
                        "The announcement comes after major controversies around GHF's operations since it took over most of the humanitarian aid distribution in the Gaza Strip on May 27 after Israel had blockaded supplies getting into the strip for more than two months.\n" +
                        "\n" +
                        "Since the end of May, at least 798 people have been killed near and around food aid sites, according to a United Nations statement on Thursday. Among them, 615 people were killed on their way to GHF sites and 183 near other aid convoys, the UN statement added.",
                url = "",
                urlToImage = "https://i.abcnewsfe.com/a/7030e39e-f3e2-42f4-a9e3-ff15f8f900ca/grand-canyon-lodge-01-rt-jef-250714_1752503864382_hpMain_1x1.jpg?w=208",
                publishedAt = "2024-07-15T10:30:45Z"
            )
        )
    }
}