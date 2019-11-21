const fs = require('fs');
const walk = require('walkdir');
const parrarel = require('async/parallel');

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

async function dir_traversal_async(dir_name) {

    await walk.async(dir_name, function (path, stat) {
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
    });
    task_counter = tasks.length;
    start = start = new Date().getTime();
    parrarel(tasks);
}

dir_traversal_async('TESTDIR');
