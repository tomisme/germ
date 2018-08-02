(ns gateworld.client.events
  (:require
   [gateworld.client.db :as db]
   [gateworld.client.events.conflict :as conflict]
   [gateworld.rules.core :as rules]
   [re-frame.core :as rf]))


(rf/reg-event-db
  :initialise
  (fn [_ _]
    db/initial-db))


(rf/reg-event-db
  :start-conflict-practise
  (fn [state _]
    (-> state
        (assoc :view :conflict)
        conflict/start-conflict-practise)))


(rf/reg-event-db
  :conflict/sacrifice-permanent
  (fn [state [_ char-idx perm-idx]]
    (-> state
        (update :conflict-state rules/add-effect {:type :sac-perm
                                                  :char-idx char-idx
                                                  :perm-idx perm-idx})
        (update :conflict-state rules/resolve-next-effect))))
