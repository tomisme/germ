(ns gateworld.client.core
  (:require
   [re-frame.core :as rf]
   [gateworld.client.db]
   [gateworld.client.events]
   [gateworld.client.subs]
   [gateworld.client.views.intro :refer [intro-view]]
   [gateworld.client.views.conflict :refer [conflict-view]]
   [gateworld.client.views.map :refer [map-view]]))

(defn ui-component
  []
  [:div
   (condp = @(rf/subscribe [:active-view])
     :conflict [conflict-view]
     :intro [intro-view]
     :map [map-view]
     [:span "Loading client..."])]) 
