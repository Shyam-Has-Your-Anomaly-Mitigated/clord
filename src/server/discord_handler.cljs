(ns server.discord-handler)

(def commands
  [
   {
    :command "ping"                                         ; This string is the command to match
    :exec    (fn [msg-obj] (.reply msg-obj "pong"))         ; The use case is the function to execute
    }
   {
    :command "hello"
    :exec    (fn [msg-obj] (.reply msg-obj "world"))
    }
   ]
  )

(defn find-command
      [str]
      (->>
        (filter #(= (:command %) str) commands)
        first))

(defn handle-command
      "This function checks the message and when it matches a
      command it executes a function provided"
      [msg]
      (let [msg-content (aget msg "content")
            command (find-command msg-content)]
           (if (nil? command)
             msg
             ((:exec command) msg))))
