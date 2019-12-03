(ns advent-of-code-2019.day03
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [clojure.set :as set]))

(defn- to-position [[d & ss]]
  (let [vs (-> ss string/join Integer/parseInt)]
    (condp = d
      \U {:direction :up :distance vs}
      \D {:direction :down :distance vs}
      \L {:direction :left :distance vs}
      \R {:direction :right :distance vs})))

(defn- to-wire [route]
  (let [directions (string/split route #",")]
    (map to-position directions)))

(def example
  (->> "R75,D30,R83,U83,L12,D49,R71,U7,L72
U62,R66,U55,R34,D71,R55,D58,R83"
      string/split-lines
      (map to-wire)))

(def data
  (->> "inputs/day03"
       io/resource
       slurp
       string/split-lines
       (map to-wire)))

(def centre
  {:current {:x 0 :y 0} :collected #{}})

(defn- vertical [x]
  (fn [acc y] (conj acc {:x x :y y})))

(defn- horizontal [y]
  (fn [acc x] (conj acc {:x x :y y})))

(defn- move [{:keys [current collected]} {:keys [direction distance]}]
  (let [{:keys [x y]} current
        coords (case direction
                 :up (reductions (vertical x) current (range (+ y 1) (+ y distance 1)))
                 :down (reductions (vertical x) current (range (- y 1) (- y distance 1) -1))
                 :left (reductions (horizontal y) current (range (- x 1) (- x distance 1) -1))
                 :right (reductions (horizontal y) current (range (+ x 1) (+ x distance 1))))]
    {:current (last coords) :collected (apply conj collected coords)}))

(defn- reduce-positions [wire]
  (-> (reduce move centre wire)
      :collected
      (disj {:x 0 :y 0})))

(defn- calc-distance  [{:keys [x y]}]
    (+ (Math/abs x) (Math/abs y)))

(defn- part01 [d]
  (let [crosses (->> d
                     (map reduce-positions)
                     (apply set/intersection)
                     (map calc-distance)
                     sort)]
    (first crosses)))
