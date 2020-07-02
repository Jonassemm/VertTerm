var moment = require('moment'); 

 /*--------------------String/Date - Translation-----------------------

    var string = "13.01.2003 13:45";
    var date = moment(string, "DD.MM.yyyy HH:mm").toDate();
    var dateString = moment(date).format("DD.MM.YYYY HH:mm").toString();
*/


export function setDate() {
    const date = new Date();

    date.setSeconds(0);

    var minutes = date.getMinutes();
    if (minutes < 15) {
        date.setMinutes(15);
    } else if (minutes < 30) {
        date.setMinutes(30);
    } else if (minutes < 45) {
        date.setMinutes(45);
    } else if (minutes <= 59) {
        date.setMinutes(0);
    }

    const resultDate = moment(date).format("DD.MM.YYYY HH:mm").toString()
    return resultDate;
}