(defproject benchmark "0.1.0-SNAPSHOT"
  :description "Simple benchmarking tool"
  :url ""
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.0"]
                 [org.clojure/data.csv "0.1.4"]]
  :main ^:skip-aot benchmark.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
