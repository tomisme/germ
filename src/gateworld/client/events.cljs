(ns gateworld.client.events
  (:require
   [gateworld.client.db :as db]
   [gateworld.client.events.combat :as combat]
   [gateworld.rules.core :as rules]
   [re-frame.core :as rf]))


(rf/reg-event-db
  :initialise
  (fn [_ _]
    db/initial-db))


(rf/reg-event-db
  :start-combat-practise
  (fn [state _]
    (-> state
        (assoc :view :combat)
        combat/start-combat-practise)))


(rf/reg-event-db
  :combat/sacrifice-permanent
  (fn [state [_ char-idx perm-idx]]
    (-> state
        (update :combat-state rules/add-effect {:type :sac-perm
                                                :char-idx char-idx
                                                :perm-idx perm-idx})
        (update :combat-state rules/resolve-next-effect))))
