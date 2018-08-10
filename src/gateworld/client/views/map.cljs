(ns gateworld.client.views.map
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


(defn grid-item-el
  [cell]
  (let [cell-size 100]
    [:div {:style {:width cell-size
                   :height cell-size
                   :background-color "green"
                   :border "2px solid black"}}]))


(defn map-grid-el
  [grid]
  (into [:div {:style {:display "flex"}}]
        (for [row grid]
          (into [:div]
                (for [cell row]
                  (grid-item-el cell))))))


(defn map-view
  [])


;;


(def example-map-state-1
  {:grid [[{} {} {} {}]
          [{} {} {} {}]
          [{} {} {} {}]
          [{} {} {} {}]]})


(defcard-rg example-map-grid-1
  (map-grid-el (:grid example-map-state-1)))


(defcard example-map-state-1 example-map-state-1)
