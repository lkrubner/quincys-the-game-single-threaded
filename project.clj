(defproject quincy_the_game_1 "1"
  :description "The rough early version of a game simulation of Quincy's Restaurant"
  :url "http://www.smashcompany.com/technology/a-parable-about-concurrency-demonstrated-with-comical-cartoons"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [
                 [org.clojure/clojure "1.6.0"]
                 [org.clojure/core.match "0.3.0-alpha4"]
                 [clj-stacktrace "0.2.8"]
                 ]
  :disable-implicit-clean true
  :warn-on-reflection true
  :main quincy-the-game-1.core
  :aot :all
  :jvm-opts ["-Xms50m" "-Xmx50m" "-XX:-UseCompressedOops"])
