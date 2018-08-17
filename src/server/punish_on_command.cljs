(ns server.punish-on-command
  (:require [server.mvf4 :as punish]
            [server.permissions :refer [check-perms]]))

(defn replace-ping
  [content, matcher, name]
  (let [result (.replace content #(str matcher name) )
        finished-replacing (.match content #(str matcher))]
    (if (nil? finished-replacing)
      result
      (recur result matcher name))))

(defn format-reason 
  [msg, user, start-index]
  (let [result (.trim (replace-ping (.substring (aget msg "content") start-index) (str #"\s*<@!{0,1}" (aget user "id") #">\s*") (aget user "displayName")))]
    (if-not (zero? (aget result "length"))
      result
      "[No reason specified.]")))

(defn get-user-to-punish
  [msg]
  (let [mentions (aget msg "mentions")
        members (aget mentions "members")]
    (if (= (aget members "size") 1)
      (.first members))))

(defn user-can-be-punished
  [msg, member]
  (let [member-is-bot (aget member "user" "bot")
        member-is-mod (check-perms msg member)]
    (if (or member-is-bot member-is-mod)
      false
      true)))

(defn warn-user
  [msg]
  (let [user (get-user-to-punish msg)]
    (if (nil? user)
      (.reply msg " you need to mention one (and only one) user for me to warn!")
      (if (user-can-be-punished msg user)
        (punish/warn msg user (format-reason msg user 5) (aget msg "member"))
        (.reply msg (str "I cannot warn "(aget user "displayName") ", as they are either (a) a bot, or (b) have Mod permissions."))))))

(defn kick-user
  [msg]
  (let [user (get-user-to-punish msg)]
    (if (nil? user)
      (.reply msg " you need to mention one (and only one) user for me to kick!")
      (if (user-can-be-punished msg user)
        (punish/kick msg user (format-reason msg user 5) (aget msg "member"))
        (.reply msg (str "I cannot kick " (aget user "displayName") ", as they are either (a) a bot, or (b) have Mod permissions."))))))

(defn ban-user
  [msg]
  (let [user (get-user-to-punish msg)]
    (if (nil? user)
      (.reply msg " you need to mention one (and only one) user for me to ban!")
      (if (user-can-be-punished msg user)
        (punish/ban msg user (format-reason msg user 4) (aget msg "member"))
        (.reply msg (str " I cannot ban " (aget user "displayName") ", as they are either (a) a bot, or (b) have Mod permissions."))))))