package com.fortysevendeg.sparkon.services.twitter

import org.slf4j.LoggerFactory
import play.Play
import services.domain.TwitterServiceException
import twitter4j.auth.OAuthAuthorization
import twitter4j.conf.ConfigurationBuilder
import twitter4j.{ Twitter, TwitterFactory }

import scala.language.postfixOps
import scala.util._

trait TwitterServices extends Serializable {

  val logger = LoggerFactory.getLogger(this.getClass)
  val woeid = 1
  //Worldwide
  lazy val twitterClient: Twitter =
    new TwitterFactory().getInstance(buildAuthorization)

  def getTrendingTopics = {
    val trends = Try(twitterClient
      .trends()
      .getPlaceTrends(woeid)
      .getTrends
      .map(_.getName)
      .toSet)

    trends match {
      case Success(trendSet) =>
        logger.info(s"Current Trending Topics => ${trendSet.mkString(", ")}")
        trendSet
      case Failure(e) => throw TwitterServiceException(e.getMessage(), e)
    }
  }

  def buildAuthorization =
    new OAuthAuthorization(new ConfigurationBuilder()
      .setOAuthConsumerKey(Play.application().configuration().getString("twitter.consumerKey"))
      .setOAuthConsumerSecret(Play.application().configuration().getString("twitter.consumerSecret"))
      .setOAuthAccessToken(Play.application().configuration().getString("twitter.accessToken"))
      .setOAuthAccessTokenSecret(Play.application().configuration().getString("twitter.accessTokenSecret"))
      .build())
}

object TwitterServices extends TwitterServices
