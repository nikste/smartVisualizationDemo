<script src="http://cdn.sockjs.org/sockjs-0.3.min.js"></script>
<script src="stomp.js"></script>
<script>

    var ws = new SockJS('http://127.0.0.1:15674/stomp');
    var client = Stomp.over(ws);

    var on_connect = function() {
            console.log('connected');
        };
        var on_error =  function() {
            console.log('error');
        };
        client.connect('guest', 'guest', on_connect, on_error, '/');
        </script>