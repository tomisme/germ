(ns gateworld.client.core
  (:require
   [re-frame.core :as rf]
   [gateworld.client.events]
   [gateworld.client.subs]
   [gateworld.client.views.combat :refer [combat-component]]
   [gateworld.client.views.intro :refer [intro-component]]))


(defn ui-component
  []
  [:div
   (condp = @(rf/subscribe [:active-view])
          :combat [combat-component]
          :intro [intro-component]
          [:span "Loading client..."])])
