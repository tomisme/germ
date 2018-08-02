(ns gateworld.client.views.intro
  (:require
   [re-frame.core :as rf])
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


(defn intro-view
  []
  [:div
   [:h1
    "Gateworld256"]
   [:ul
    [:li
     [:a {:style {:cursor "pointer"}
          :on-click #(rf/dispatch [:start-conflict-practise])}
      "Conflict Practise"]]]])
