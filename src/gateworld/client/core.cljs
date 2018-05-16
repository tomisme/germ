(ns gateworld.client.core
  (:require
   [gateworld.rules.core]
   [reagent.core :as reagent]
   [re-frame.core :as rf]))


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
   :story ["a" "b" "c"]
   :world {:cards [(-> rock-card
                       (assoc :owner-pid 0)
                       (assoc :pos :hand))
                   (-> rock-card
                       (assoc :owner-pid 0)
                       (assoc :pos :field))
                   (-> blob-card
                       (assoc :owner-pid 0)
                       (assoc :pos :field))
                   (-> blob-card
                       (assoc :owner-pid 0)
                       (assoc :pos :field))
                   (-> blob-card
                       (assoc :owner-pid 1)
                       (assoc :pos :field))
                   (-> blob-card
                       (assoc :owner-pid 1)
                       (assoc :pos :hand))]}})


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
    (->> db
         :world
         :cards
         (filter #(= (:pos %) :field))
         (filter #(= (:owner-pid %) pid)))))


(rf/reg-sub
  :hand-cards
  (fn [db [_ pid]]
    (->> db
         :world
         :cards
         (filter #(= (:pos %) :hand))
         (filter #(= (:owner-pid %) pid)))))


;;


(defn field-permanent-el
  [{:keys [name attack toughness owner-pid]}]
  [:div {:style {:padding 10
                 :margin 5
                 :border "1px solid"}}
   [:div name]
   (when (or attack toughness)
     [:div attack "/" toughness])])


(defn field-el
  [pid]
  (into [:div {:style {:display "flex"
                       :margin 10}}]
        (map field-permanent-el @(rf/subscribe [:field-cards pid]))))


(defn hand-el
  []
  [:div]
  (into [:div {:style {:display "flex"
                       :margin 10}}]
        (map field-permanent-el @(rf/subscribe [:hand-cards 0]))))


(defn env-el
  []
  [:div {:style {:padding 10
                 :border "1px solid"}}
   [field-el 1]
   [:hr]
   [field-el 0]
   [:hr]
   [:hr]
   [hand-el]])


(defn story-el
  []
  (into [:div {:style {:min-width 200
                       :border "1px solid"
                       :margin-right 10
                       :padding 10}}]
        (map (fn [s]
               [:div (str "~ " s)])
             @(rf/subscribe [:story]))))


(defn ui-el
  []
  [:div {:style {:border "1px solid"
                 :padding 10
                 :display "flex"}}
   [story-el]
   [env-el]])


;;
;
;
; (defn render-ui
;   []
;   (reagent/render [ui-el] (js/document.getElementById "app")))
;
;
; (defonce start
;   (do
;    (rf/dispatch-sync [:initialise])
;    (render-ui)))
;
;
; (render-ui)
