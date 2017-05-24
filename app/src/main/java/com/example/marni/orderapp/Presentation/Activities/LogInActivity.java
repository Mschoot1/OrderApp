var express = require("express");
var mysql = require('mysql');
var app = express();
var bodyParser = require('body-parser');
var expressJWT = require('express-jwt');
var jwt = require('jsonwebtoken');
var bcrypt = require('bcrypt');
var salt = bcrypt.genSaltSync(10);
var _ = require('lodash');

var db_config = {
    host: 'eu-cdbr-west-01.cleardb.com',
    user: 'b7097498a52cf9',
    password: '418717e7',
    database: 'heroku_c8fc5906c0a0752',
    multipleStatements: true
};

var connection;

function handleDisconnect() {
    console.log('connecting to db');
    connection = mysql.createConnection(db_config);

    connection.connect(function(err) {
        if (err) {
            console.log('2. error when connecting to db:', err);
            setTimeout(handleDisconnect, 1000);
        }
    });
    connection.on('error', function(err) {
        console.log('3. db error', err);
        if (err.code === 'PROTOCOL_CONNECTION_LOST') {
            handleDisconnect();
        } else {
            throw err;
        }
    });
}

handleDisconnect();

app.use( bodyParser.json() );
app.use(bodyParser.urlencoded({
    extended: true
}));

app.use(expressJWT({ secret: 'zeersecret'}).unless({ path: ['/loginAuth', '/loginRegister', '/register', /^\/order.*/, /^\/products.*/, /^\/account.*/, /^\/customer.*/]}));

app.get('/secret', function(request, response) {
    connection.query('SELECT * from secret', function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.send(JSON.stringify({"results": results}));
    });
});

app.get('/secret2', function(request, response) {
    connection.query('SELECT * from secret', function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.send(JSON.stringify({"results": results}));
    });
});

app.get('/product/allergies', function(request, response) {
    connection.query('SELECT * FROM allergies', function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});

app.get('/orders/:user', function(request, response) {
    connection.query('SELECT * FROM orders WHERE customer_id=? ORDER BY status, id DESC', [request.params.user], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});

app.get('/order/:id', function(request, response) {
    connection.query('SELECT * FROM orders WHERE id=?', [request.params.id], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});

app.get('/products/:user/category/:category', function(request, response) {
    connection.query('(SELECT product_orders.product_id AS id, product_orders.quantity, products.name, products.price, products.size, products.alcohol, products.category_id as category_id, product_category.name as category_name FROM orders JOIN product_orders ON (product_orders.order_id = orders.id) JOIN products ON products.id = product_orders.product_id JOIN product_category ON product_category.id = products.category_id AND products.category_id = ? WHERE orders.status = 0 AND orders.customer_id = ? ORDER BY category_id ) UNION ( SELECT products.id, 0 AS quantity, products.name, products.price, products.size, products.alcohol, products.category_id, product_category.name as category_name FROM products JOIN product_category ON product_category.id = products.category_id AND products.category_id = ? WHERE products.id NOT IN ( SELECT product_orders.product_id FROM orders JOIN product_orders ON (product_orders.order_id = orders.id) JOIN products ON products.id = product_orders.product_id WHERE orders.status = 0 AND orders.customer_id = ? ) ) ORDER BY category_id, id', [request.params.category, request.params.user, request.params.category, request.params.user, ], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});

app.get('/products/:user', function(request, response) {
    function mergeByProductId(arr) {
        return _(arr)
            .groupBy(function(item) {
                return item.id;
            })
            .map(function(group) {
                return _.mergeWith.apply(_, [{}].concat(group, function(obj, src) {

                    if (Array.isArray(obj)) {
                        return obj.concat(src);
                    }
                }))
            })
            .orderBy(['category_id'], ['asc'])
            .values()
            .value();
    }
    connection.query({sql: '(SELECT product_orders.product_id AS id, product_orders.quantity, products.name, products.price, products.size, products.alcohol, products.category_id as category_id, product_category.name as category_name, allergies.description, allergies.image FROM orders JOIN product_orders ON (product_orders.order_id = orders.id) JOIN products ON products.id = product_orders.product_id JOIN product_category ON product_category.id = products.category_id LEFT JOIN product_allergy ON product_allergy.product_id=products.id LEFT JOIN allergies ON allergies.id=product_allergy.allergy_id WHERE orders.status = 0 AND orders.customer_id = ? ORDER BY products.category_id ) UNION (SELECT products.id, 0 AS quantity, products.name, products.price, products.size, products.alcohol, products.category_id, product_category.name as category_name, allergies.description, allergies.image FROM products JOIN product_category ON product_category.id = products.category_id LEFT JOIN product_allergy ON product_allergy.product_id=products.id LEFT JOIN allergies ON allergies.id=product_allergy.allergy_id WHERE products.id NOT IN (SELECT product_orders.product_id FROM orders JOIN product_orders ON (product_orders.order_id = orders.id) JOIN products ON products.id = product_orders.product_id WHERE orders.status = 0 AND orders.customer_id = ?) ) ORDER BY category_id, id', nestTables: true }, [request.params.user, request.params.user], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        results.forEach(function(row) {

            row.id = row[''].id;
            row.name = row[''].name;
            row.price = row[''].price;
            row.size = row[''].size;
            row.alcohol = row[''].alcohol;
            row.category_id = row[''].category_id;
            row.category_name = row[''].category_name;
            row.quantity = row[''].quantity;
            row.allergies = [].concat({ description: row[''].description, image: row[''].image });

            delete row[''];
        });

        response.end(JSON.stringify({"results": mergeByProductId(results)}));
    });
});

app.get('/products/order/:id', function(request, response) {
    function mergeByProductId(arr) {
        return _(arr)
            .groupBy(function(item) {
                return item.product_id;
            })
            .map(function(group) {
                return _.mergeWith.apply(_, [{}].concat(group, function(obj, src) {

                    if (Array.isArray(obj)) {
                        return obj.concat(src);
                    }
                }))
            })
            .orderBy(['category_id'], ['asc'])
            .values()
            .value();
    }
    connection.query({sql: 'SELECT products.*, product_category.name as category_name, product_orders.*, allergies.description, allergies.image FROM orders INNER JOIN product_orders ON product_orders.order_id=orders.id LEFT JOIN products ON products.id=product_orders.product_id LEFT JOIN product_category ON products.category_id=product_category.id LEFT JOIN product_allergy ON product_allergy.product_id=products.id LEFT JOIN allergies ON allergies.id=product_allergy.allergy_id WHERE orders.id = ? ORDER BY products.category_id, products.id', nestTables: true }, [request.params.id], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        results.forEach(function(row) {

            row.id = row['product_orders'].id;
            row.name = row['products'].name;
            row.price = row['products'].price;
            row.size = row['products'].size;
            row.alcohol = row['products'].alcohol;
            row.category_id = row['products'].category_id;
            row.category_name = row['product_category'].category_name;
            row.order_id = row['product_orders'].order_id;
            row.product_id = row['product_orders'].product_id;
            row.customer_id = row['product_orders'].customer_id;
            row.quantity = row['product_orders'].quantity;
            row.timestamp = row['product_orders'].timestamp;
            row.allergies = [].concat(row['allergies']);

            delete row['product_orders'];
            delete row['products'];
            delete row['product_category'];
        });

        response.end(JSON.stringify({"results": mergeByProductId(results)}));
    });
});

app.get('/order/current/:user', function(request, response) {
    connection.query('SELECT * FROM orders WHERE status=0 AND customer_id=?', [request.params.user], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});

app.put('/order/price/edit', function(request, response) {
    connection.query('UPDATE `orders` SET `price_total`=? WHERE `id`=?', [request.body.price_total, request.body.order_id], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});
app.put('/product/quantity/edit', function(request, response) {
    connection.query('UPDATE `product_orders` SET `quantity`=? WHERE `product_id`=? AND customer_id=? AND order_id = ?', [request.body.quantity, request.body.product_id, request.body.customer_id, request.body.order_id], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});

app.delete('/product/quantity/delete', function(request, response) {
    connection.query('DELETE FROM `product_orders` WHERE `product_id`=? AND customer_id=? AND order_id = ?', [request.body.product_id, request.body.customer_id, request.body.order_id], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});

app.put('/order/edit', function(request, response) {
    connection.query('UPDATE `orders` SET `status`=1 WHERE `id`=?', [request.body.id], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});

app.post('/product/quantity/add', function (request, res) {
    var postData  = { order_id: request.body.order_id, product_id: request.body.product_id, customer_id: request.body.customer_id, quantity: request.body.quantity};
    connection.query('INSERT INTO product_orders SET ?', postData, function (error, results, fields) {
        console.log(postData);
        if (error) throw error;
        res.end(JSON.stringify(results));
    });
});

app.get('/customers', function(request, response) {
    connection.query('SELECT * from customers', function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});

app.get('/account/:user', function(request, response) {
    connection.query('SELECT email, balance from customers WHERE id=?', [request.params.user], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});


app.get('/email/:user', function(request, response) {
    connection.query('SELECT email from customers WHERE id=?', [request.params.user], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify(results));
    });
});

app.get('/customers/:id?', function (req, res) {
    connection.query('select * from customers where id=?', [req.params.id], function (error, results, fields) {
        if (error) throw error;
        res.end(JSON.stringify(results));
    });
});

app.post('/register', function (req, res) {
    connection.query('INSERT INTO customers SET email =?, password = ?;INSERT INTO orders (status, price_total, customer_id) SELECT 0, 0, id FROM customers WHERE email = ?;INSERT INTO device_information (customer_id) SELECT id FROM customers WHERE email = ?', [req.body.email, bcrypt.hashSync(req.body.password, salt), req.body.email, req.body.email], function (error, results, fields) {
        if (error) throw error;
        res.end(JSON.stringify(results));
    });
});

app.post('/topup', function (req, res) {
    connection.query('INSERT INTO balance_history SET `credit`=?, `type`=?,`customer_id`=?;UPDATE `customers` SET `balance`= `balance` + ? WHERE `id`=?', [req.body.credit, req.body.type, req.body.customer_id, req.body.credit, req.body.customer_id], function (error, results, fields) {
        if (error){
            throw error;
        } else {
            res.end(JSON.stringify(results));
        }
    });
});

app.post('/order/pay', function (req, res) {
    connection.query('INSERT INTO balance_history SET `credit`=?,`customer_id`=?;UPDATE `customers` SET `balance`= `balance` - ? WHERE `id`=?;INSERT INTO orders SET `status`=0, `price_total`=0, `customer_id`=?', [req.body.credit, req.body.customer_id, req.body.credit, req.body.customer_id, req.body.customer_id], function (error, results, fields) {
        if (error){
            throw error;
        } else {
            res.end(JSON.stringify(results));
        }
    });
});

app.put('/customer/device', function (req, res) {
    connection.query('UPDATE `device_information` SET `hardware`=?, `type`=?, `model`=?, `brand`=?, `device`=?, `manufacturer`=?, `user`=?, `serial`=?, `host`=?, `device_id`=?, `bootloader`=?, `board` =?, `display`=? WHERE `customer_id`=?', [req.body.hardware, req.body.type, req.body.model, req.body.brand, req.body.device, req.body.manufacturer, req.body.user, req.body.serial, req.body.host, req.body.device_id, req.body.bootloader, req.body.board, req.body.display, req.body.customer_id]
        , function (error, results, fields) {
        if (error){
            throw error;
        } else {
            res.end(JSON.stringify(results));
        }
    });
});

app.get('/customer/:user/device/', function(request, response) {
    connection.query('SELECT * from device_information WHERE customer_id=?', [request.params.user], function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});

app.get('/product/categories', function(request, response) {
    connection.query('SELECT * FROM product_category', function(err, results, fields) {
        if (err) {
            console.log('error: ', err);
            throw err;
        }
        response.end(JSON.stringify({"results": results}));
    });
});

app.post('/login', function (req, res) {
    connection.query('SELECT * FROM customers WHERE email =?', [req.body.email], function (error, results, fields) {
        if (error) {
            throw error;
        } else {
            if(results.length > 0){
                if( bcrypt.compareSync(req.body.password, results[0].password) ) {
                    res.sendStatus(200);
                } else {
                    res.sendStatus(401);
                }
            } else {
                res.sendStatus(401);
            }
        }
    });
});

app.post('/loginAuth', function (req, res) {
    connection.query('SELECT * FROM customers WHERE email =?', [req.body.email], function (error, results, fields) {
        if (error) {
            throw error;
        } else {
            if(results.length > 0){
                if( bcrypt.compareSync(req.body.password, results[0].password) ) {
                    var token = jwt.sign({ user: results[0].id }, 'zeersecret');
                    res.status(200).json(token);
                } else {
                    res.sendStatus(401);
                }
            } else {
                res.sendStatus(401);
            }
        }
    });
});

app.post('/loginRegister', function (req, res) {
    connection.query('SELECT * FROM register WHERE id =?', [req.body.email], function (error, results, fields) {
        if (error) {
            throw error;
        } else {
            if(results.length > 0){
                if( bcrypt.compareSync(req.body.password, results[0].password) ) {
                    var token = jwt.sign({ user: results[0].id }, 'zeersecret');
                    res.status(200).json(token);
                } else {
                    res.sendStatus(401);
                }
            } else {
                res.sendStatus(401);
            }
        }
    });
});

app.put('/customers', function (req, res) {
    connection.query('UPDATE `customers` SET `email`=?,`password`=? where `id`=?', [req.body.email,req.body.password, req.body.id], function (error, results, fields) {
        if (error) throw error;
        res.end(JSON.stringify(results));
    });
});

app.delete('/customers', function (req, res) {
    console.log(req.body);
    connection.query('DELETE FROM `customers` WHERE `id`=?', [req.body.id], function (error, results, fields) {
        if (error) throw error;
        res.end('Deleted');
    });
});
var port = process.env.PORT || 9998;
app.listen(port, function() {
    console.log("Listening on " + port);
});
