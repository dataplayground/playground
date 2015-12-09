var myApp = angular.module('myApp', ['ngWebSocket']);

myApp.factory('MyData', function ($websocket) {
        // Open a WebSocket connection
        var dataStream = $websocket('ws://localhost:9000/socket');

        var collection = [];

        dataStream.onMessage(function (message) {
            collection.push(JSON.parse(message.data));
        });

        var methods = {
            collection: collection,
            get: function (searchParam) {
                dataStream.send(JSON.stringify({foo: searchParam}));
            }
        };

        return methods;
    })
    .controller('SomeController', function ($scope, MyData) {
    //TODO how to get rid of this without an error?
    });


myApp.controller('MainCtrl', function ($scope, MyData) {

    $scope.list = [];
    $scope.text = 'spark';

    $scope.MyData = MyData;

    $scope.filterForm = {};
    $scope.filterForm.keyword = "spark";

    $scope.submit = function () {
        if ($scope.filterForm.keyword) {
            $scope.list.push(this.filterForm.keyword);
            MyData.get(this.filterForm.keyword);
            $scope.filterForm.keyword = '';
        }
    };
});
