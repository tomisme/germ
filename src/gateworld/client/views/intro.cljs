(ns gateworld.client.views.intro
  (:require
   [re-frame.core :as rf])
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


(defn intro-component
  []
  [:div
   [:h1
    "Gateworld256"]
   [:ul
    [:li
     "World Map"]
    [:li
     [:a {:style {:cursor "pointer"}
          :on-click #(rf/dispatch [:start-combat-practise])}
      "Combat Practise"]]
    [:li
     "World Editor"]]])
