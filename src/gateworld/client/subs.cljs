(ns gateworld.client.subs
  (:require
   [re-frame.core :as rf]))


(rf/reg-sub
  :active-view
  (fn [db _]
    (:view db)))


(rf/reg-sub
  :story-items
  (fn [db _]
    (:story db)))


(rf/reg-sub
  :field-cards
  (fn [db [_ char-idx]]
    (get-in db [:combat-state :chars char-idx :permanents])))


(rf/reg-sub
  :hand-cards
  (fn [db [_ char-idx]]
    (get-in db [:combat-state :chars char-idx :in-hand])))
