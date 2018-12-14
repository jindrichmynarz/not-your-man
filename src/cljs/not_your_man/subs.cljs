(ns not-your-man.subs
  "Subscriptions should allow views not to have a clue about the database schema.
  However, in simple cases they end up as trivial lookups."
  (:require [re-frame.core :refer [reg-sub]]
            [goog.string :refer [format]]))

(reg-sub ::error (comp :status-text :error))

(reg-sub ::loading? :loading?)

(reg-sub ::modal? (some-fn :error :loading?))

(reg-sub ::subject :subject)

(reg-sub ::verdict
  (fn [{:keys [label subject]}]
    (format "He's not your %s. He's %s." subject label)))

(reg-sub ::not-your-man :not-your-man)
