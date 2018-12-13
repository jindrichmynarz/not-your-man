(ns not-your-man.subs
  (:require [re-frame.core :as re-frame]))

(re-frame/reg-sub
  ::error
  (fn [{:keys [error]}] error))

(re-frame/reg-sub
  ::loading?
  (fn [{:keys [loading?]}] loading?))

(re-frame/reg-sub
  ::modal?
  (fn [{:keys [error loading?]}]
    (or error loading?)))

(re-frame/reg-sub
  ::subject
  (fn [{:keys [subject]}] subject))

(re-frame/reg-sub
  ::actual-subject
  (fn [{:keys [label]}] label))

(re-frame/reg-sub
  ::not-your-man
  (fn [{:keys [not-your-man]}] not-your-man))
