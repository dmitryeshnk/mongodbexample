ДЛЯ КАЖДОГО БЛЮДА ПОЛУЧИТЬ КОЛИЧЕСТВО ПОРЦИЙ ПО МЕСЯЦАМ:
{ $unwind: "$meals"},
Развернул массив
{ $group: { _id: { month: { $month: "$date" } }, serv: { $addToSet: "$meals.name" }, meals: { $push: "$meals" } } },
группирую по месяцам, так же добавляю уникальные имена блюд и все заказы
{ $unwind: "$serv" },
развернул массив с уникальными именами групп
{ $project: { month: "$_id.month", mealName: "$serv", meals: { $filter: { input: "$meals", as: "array", cond: { $eq: ["$$array.name", "$serv"] } } } } },
в каждой записи оставляю имя блюда и все заказы за месяц с этим блюдом
{ $sort: { month: 1 } },
сортирую по месяцам
{ $group: { _id: "$mealName", stats: { $push: { month: "$month", servings: { $sum: "$meals.servings" } } } } }
для каждого блюда считаю количество порций


ДЛЯ КАЖДОГО БЛЮДА ПОЛУЧИТЬ КОЛИЧЕСТВО ЗАКАЗОВ ПО МЕСЯЦАМ:
{ $unwind: "$meals"},
Развернул массив
{ $group: { _id: { month: { $month: "$date" } }, serv: { $addToSet: "$meals.name" }, meals: { $push: "$meals" } } },
группирую по месяцам, так же добавляю уникальные имена блюд и все заказы
{ $unwind: "$serv" },
развернул массив с уникальными именами групп
{ $project: { month: "$_id.month", mealName: "$serv", meals: { $filter: { input: "$meals", as: "array", cond: { $eq: ["$$array.name", "$serv"] } } } } },
в каждой записи оставляю имя блюда и все заказы за месяц с этим блюдом
{ $sort: { month: 1 } },
сортирую по месяцам
{ $group: { _id: "$mealName", stats: { $push: { month: "$month", orders: { $size: "$meals" } } } } }
для каждого блюда считаю количество заказов с этими блюдами



ОПРЕДЕЛИТЬ ТОП 3 САМЫХ ПРОДАВАЕМЫХ БЛЮД ПО МЕСЯЦАМ:
{ $unwind: "$meals" },
развернул массив
{ $group: { _id: { name: "$meals.name", month: { $month: "$date" } }, servingsCount: { $sum: "$meals.servings" } } },
группирую по имени и месяцу
{ $sort: { servingsCount: -1 } },
сортирую по количеству порций по убыванию
{ $group: { _id: "$_id.month", topMeals: { $push: { meal: "$_id.name", servingsSold: "$servingsCount" } } } },
для каждого месяца формирую массив topMeals
{ $project: { top3meals: { $slice: ["$topMeals", 3] } } },
оставляю только топ 3 блюда
{ $sort: { "_id": 1 } }
сортирую по месяцам
