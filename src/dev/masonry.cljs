(ns dev.masonry
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))

;; https://masonry.desandro.com/methods.html

(defn item-el []
  [:div.grid-item
   {:style {:width 150
            :height 50
            :border "2px solid"
            :margin 2}}])

(defcard-rg item
  (item-el))

(defcard-rg grid
  (let [go #(do
             (let [grid (.querySelector js/document ".grid")]
               (js/Masonry. grid #js{"itemSelector" ".grid-item"})))]
    [:div
     [:button {:on-click go}
      "new Masonry"]
     [:div.grid
      (item-el)
      (item-el)
      (item-el)
      (item-el)
      (item-el)
      (item-el)
      (item-el)
      (item-el)
      (item-el)
      (item-el)]]))
