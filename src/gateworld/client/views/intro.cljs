(ns gateworld.client.views.intro
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


(defn intro-el
  []
  [:div
   [:h1
    "Gateworld"]
   [:ul
    [:li
     "World Map"]
    [:li
     "Combat Practise"]
    [:li
     "World Editor"]]])


(defcard-rg intro-view (intro-el))
