'use strict';

bikeManApp
    .config(['$stateProvider', '$httpProvider', '$translateProvider', 'USER_ROLES',
        function ($stateProvider, $httpProvider, $translateProvider, USER_ROLES) {
            $stateProvider
                .state('transaction', {
                    url: '/transaction',
                    templateUrl: 'views/transactions.html',
                    controller: 'TransactionController',
                    resolve:{
                        resolvedTransaction: ['Transaction', function (Transaction) {
                            return Transaction.query();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .state('closedTransaction', {
                    url: '/closed-transaction',
                    templateUrl: 'views/transactions.html',
                    controller: 'TransactionController',
                    resolve:{
                        resolvedTransaction: ['Transaction', function (Transaction) {
                            return Transaction.queryClosedTransactions();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
                .state('openTransaction', {
                    url: '/open-transaction',
                    templateUrl: 'views/transactions.html',
                    controller: 'TransactionController',
                    resolve:{
                        resolvedTransaction: ['Transaction', function (Transaction) {
                            return Transaction.queryOpenTransactions();
                        }]
                    },
                    access: {
                        authorizedRoles: [USER_ROLES.all]
                    }
                })
        }]);
