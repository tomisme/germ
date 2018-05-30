(ns gateworld.client.core
  (:require
   [gateworld.rules.core :as rules]
   [gateworld.rules.effects :as effects]
   [reagent.core :as reagent]
   [re-frame.core :as rf]
   [re-frame.db])
  (:require-macros
   [devcards.core :refer [defcard defcard-rg]]))


;;


(def rock-card
  {:name "Rock"})


(def blob-card
  {:name "Blob"
   :attack 1
   :toughness 1})


;;


(def initial-db
  {:view :combat
   :story ["Combat started between _ and _"]
   :combat-state (-> rules/empty-combat-state
                     (effects/add-char rules/empty-char)
                     (effects/add-char rules/empty-char)
                     (effects/give-char-permanent 0 rock-card)
                     (effects/give-char-permanent 0 blob-card)
                     (effects/give-char-permanent 0 blob-card)
                     (effects/give-char-permanent 1 blob-card)
                     (effects/give-char-in-hand 0 rock-card))})


;;


(rf/reg-event-db
  :initialise
  (fn [_ _]
    initial-db))


(rf/reg-sub
  :story
  (fn [db _]
    (:story db)))


(rf/reg-sub
  :field-cards
  (fn [db [_ pid]]
    (-> db
        :combat-state
        :chars
        (get pid)
        :permanents)))


(rf/reg-sub
  :hand-cards
  (fn [db [_ pid]]
    (-> db
         :combat-state
         :chars
         (get pid)
         :in-hand)))


;;


(defn field-permanent-el
  [{:keys [name attack toughness owner-pid]}]
  [:div {:style {:padding 10
                 :margin 5
                 :border "1px solid"}}
   [:div name]
   (when (or attack toughness)
     [:div attack "/" toughness])])


(defn field-component
  [pid]
  (into [:div {:style {:display "flex"
                       :margin 10}}]
        (map field-permanent-el @(rf/subscribe [:field-cards pid]))))


(defn hand-component
  []
  [:div]
  (into [:div {:style {:display "flex"
                       :margin 10}}]
        (map field-permanent-el @(rf/subscribe [:hand-cards 0]))))


(defn env-component
  []
  [:div {:style {:padding 10
                 :border "1px solid"}}
   [field-component 1]
   [:hr]
   [field-component 0]
   [:hr]
   [:hr]
   [hand-component]])


(defn story-component
  []
  (into [:div {:style {:min-width 200
                       :border "1px solid"
                       :margin-right 10
                       :padding 10}}]
        (map (fn [s]
               [:div (str "~ " s)])
             @(rf/subscribe [:story]))))


(defn ui-component
  []
  [:div {:style {:border "1px solid"
                 :padding 10
                 :display "flex"}}
   [story-component]
   [env-component]])


;;


(rf/dispatch-sync [:initialise])


;;


; (reagent/render [ui-component] (js/document.getElementById "app"))


;;


(defcard-rg ui-component
  [ui-component])


(defcard app-db
  @re-frame.db/app-db)
