db.createUser(
    {
      user: "trip",
      pwd: "trip",
      roles: [
        {
          role: "readWrite",
          db: "trip-database"
        }
      ]
    }
);