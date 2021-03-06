Filtered Hatebu
===============

- TravisCI [![Build Status](https://travis-ci.org/gen0083/FilteredHatebu.svg?branch=develop)](https://travis-ci.org/gen0083/FilteredHatebu)
- CircleCI [![CircleCI](https://circleci.com/gh/gen0083/FilteredHatebu/tree/develop.svg?style=svg)](https://circleci.com/gh/gen0083/FilteredHatebu/tree/develop)

---

個人的な勉強・練習用に作ったはてブリーダーアプリです。アプリはGoogle Playで公開しています。

<a href='https://play.google.com/store/apps/details?id=jp.gcreate.product.filteredhatebu&utm_source=global_co&utm_medium=prtnr&utm_content=Mar2515&utm_campaign=PartBadge&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img width="200" alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png'/></a>

MVPっぽい設計、Daggerを使ったモジュールの差し替え、テスト、などを目的としています。

質問・要望・アドバイスなどはissueに登録、もしくはTwitter@gen0083までお願いします。

## Libraries

使用しているライブラリ（詳しくはapp/build.gradleを確認してください）

- [Dagger2](https://github.com/google/dagger)
- [Gson](https://github.com/google/gson)
- [OkHttp3](https://github.com/square/okhttp)
- [Retrofit2](https://github.com/square/retrofit)
- [Moshi](https://github.com/square/moshi)
- [Picasso](https://github.com/square/picasso)
- [LeakCanary](http://github.com/square/leakcanary)
- [SimpleXml](http://simple.sourceforge.net)
- [Android Orma](https://github.com/gfx/Android-Orma)
- [RxJava](https://github.com/ReactiveX/RxJava)
- [RxAndroid](https://github.com/ReactiveX/RxAndroid)
- [ThreeTenAbp](https://github.com/JakeWharton/ThreeTenABP/)
- [Timber](https://github.com/JakeWharton/timber)

### Gradle Plugins

- [Android License Tools Plugin](https://github.com/cookpad/license-tools-plugin)　OSSライセンス表記に便利
- [Ribbonizer plugin for Android](https://github.com/gfx/gradle-android-ribbonizer-plugin)　アプリアイコンの識別に重宝
- [gradle-play-publisher](https://github.com/Triple-T/gradle-play-publisher)　APKのリリースをGradleで実行

## デプロイ

`./script/deploy_local.sh`を実行する

release_note.txtを更新していないとスクリプトの実行が失敗するようになっているので、更新内容を書き忘れてアプリが公開されることは防げる。

