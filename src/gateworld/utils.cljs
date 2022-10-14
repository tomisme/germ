(ns gateworld.utils)


(defn log
  [x]
  (.log js/console x))


(defn vec-without-idx
  [v i]
  (into (subvec v 0 i)
        (subvec v (inc i))))
