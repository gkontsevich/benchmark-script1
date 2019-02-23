(ns benchmark.core
  (:require [clojure.java.shell :refer :all]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io])
  (:gen-class))

(defn run-benchmark
  "Runs a benchmark and returns the result"
  [model-multiplier]
  (sh "time" "-f" "%U"
      "./BatchMatchModelFast" "models" (str model-multiplier)
      :env {"LD_LIBRARY_PATH" "."})) ;; so it finds the lib in the same dir

(defn get-runtime
  [number-of-runs]
  (let [program-run (run-benchmark number-of-runs)]
    (if (zero? (:exit program-run))
      (Double/parseDouble(:err program-run))
      (println "Program crashed :("))))

(defn try-all-run-numbers
  [max-number-of-runs step-size]
  (if (< max-number-of-runs 1) {:num-models [] :run-times []}
      (let [{:keys [num-models run-times]} (try-all-run-numbers (- max-number-of-runs step-size)
                                                              step-size)
            new-run-time (get-runtime max-number-of-runs)]
        {:num-models (conj num-models max-number-of-runs)
         :run-times (conj run-times new-run-time)})))

;; modified from: http://clojuredocs.org/clojure.core/with-open
(defn write-benchmark-to-csv
  "Writes a csv file using a key and an s-o-s (sequence of sequences)"
  [benchmark-result out-file]

     ;; overwrites the existing file with an empty one
  (spit out-file "" :append false)

  (with-open [file-writer (io/writer out-file)]
    (csv/write-csv file-writer
                   [(:num-models benchmark-result) (:run-times benchmark-result)])))

(defn benchmark-to-csv
  "Runs the benchmarks and prints out the result to a CSV file"
  [model-multiplier step-size]
  (write-benchmark-to-csv
   (try-all-run-numbers model-multiplier step-size)
   "benchmark.csv"))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (let [model-multiplier (Integer/parseInt (first args))
        step-size (Integer/parseInt (second args))]
   (benchmark.core/benchmark-to-csv model-multiplier step-size))
  (System/exit 0))
