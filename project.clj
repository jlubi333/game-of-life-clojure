(defproject life "0.1.0-SNAPSHOT"
  :description "Conway's Game of Life in Clojure"
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  :dependencies [[org.clojure/clojure "1.5.1"]]
  :main ^:skip-aot life.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
