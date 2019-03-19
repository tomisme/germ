(ns gateworld.dev.images
  (:require-macros
    [devcards.core :refer [defcard defcard-rg]]))


(def rules-symbols
  {:icon/ruling "icons/noun_1710142_cc.svg"})


;;


(defcard rules-symbols
  rules-symbols)


(defcard ruling-icon
  (:icon/ruling rules-symbols))


(defcard-rg svg-rules-symbol
  [:div
   [:img {:style {:width 20}
          :src (get rules-symbols :icon/ruling)}]
   [:img {:style {:width 100}
          :src (get rules-symbols :icon/ruling)}]
   [:img {:style {:width 400}
          :src (get rules-symbols :icon/ruling)}]])
