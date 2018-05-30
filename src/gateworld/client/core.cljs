(ns gateworld.client.core
  (:require
   [re-frame.core :as rf]
   [gateworld.client.db]
   [gateworld.client.events]
   [gateworld.client.subs]
   [gateworld.client.views.intro :refer [intro-component]]
   [gateworld.client.views.combat :refer [combat-component]]))


(defn ui-component
  []
  [:div
   (condp = @(rf/subscribe [:active-view])
          :combat [combat-component]
          :intro [intro-component]
          [:span "Loading client..."])])
