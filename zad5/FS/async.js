const fs = require('fs');
const walk = require('walkdir');

var tasks = [];
var counter = 0;
var task_counter = 0;
var start = 0;

function callback() {
    task_counter--;
    if (task_counter === 0) {
        console.log(counter);
        console.log("Calculation took " + (start = new Date().getTime() - start) + " milliseconds.");
    }
};

function dir_traversal_async(dir_name) {

    start = new Date().getTime();
    walk(dir_name, function (path, stat) {
        if (stat.isFile()) {
            tasks.push(async function count_lines() {
                var count = 0;
                fs.createReadStream(path).on('data', function (chunk) {
                    count += chunk.toString('utf8')
                        .split(/\r\n|[\n\r\u0085\u2028\u2029]/g)
                        .length - 1;
                }).on('end', function () {
                    console.log(path, count);
                    counter += count;
                    callback();
                }).on('error', function (err) {
                    console.error(path);
                });
            });
        }
    }).on('end', function(){
        task_counter = tasks.length;
        for (index = 0, len = tasks.length; index < len; ++index) {
           tasks[index]();
        }
    });
}

dir_traversal_async('TESTDIR');
