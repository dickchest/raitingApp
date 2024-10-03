# Time Tracking Application

## Overview
Time Tracking Application is a comprehensive tool designed to help individuals and teams manage their time efficiently. Whether you're tracking time for personal projects or professional tasks, our application provides a user-friendly interface and robust features to ensure you stay on top of your schedule.

## Features

- **User Authentication**: Securely log in and manage your account.
- **Category Management**: Organize your tasks into customizable categories.
- **Time Entry Logging**: Record and track time spent on various activities.
- **Reviews**: Provide and manage feedback for continuous improvement.
- **Reporting**: Gain insights with detailed reports on time distribution by category and week.
- **Top Users Ranking**: View and manage a leaderboard of top users based on time entries.

## Getting Started

To get started with the Time Tracking Application, follow the steps below:

1. **Set up the project**: Clone the repository to your local machine and navigate to the project directory.

```sh
    git clone https://github.com/your-username/time-tracking-app.git
    cd time-tracking-app
```
    
2. **Install dependencies**: Run the following command to install the necessary dependencies.

```sh
mvn install
```

3. **Configure Firebase**: Ensure you have a Firebase project set up and configure the application with your Firebase credentials.


4. **Start the application**: Run the application using your IDE or the following command.

```sh
   mvn spring-boot:run
```

5. Access the API: The application's endpoints will be available at http://localhost:8080/.


## API Endpoints

### Authentication
- `GET /auth/getUid`: Get the current user's UID.
- `GET /auth/getEmail`: Get the current user's email.
- `GET /auth/getAllUid`: Get a list of all user UIDs.
- `POST /auth/create`: Create a new user.
- `PUT /auth/update`: Update an existing user.
- `GET /auth/getUser`: Get user details by document ID.

### Categories
- `GET /categories/getAll`: Get all categories.
- `POST /categories/create`: Create a new category.
- `GET /categories/get`: Get category details by document ID.
- `PUT /categories/update`: Update an existing category.
- `DELETE /categories/delete`: Delete a category by document ID.

### Reviews
- `GET /reviews/getAll`: Get all reviews.
- `POST /reviews/create`: Create a new review.
- `GET /reviews/get`: Get review details by document ID.
- `PUT /reviews/update`: Update an existing review.
- `DELETE /reviews/delete`: Delete a review by document ID.

### Time Entries
- `GET /time/getAll`: Get all time entries.
- `POST /time/create`: Create a new time entry.
- `GET /time/get`: Get time entry details by document ID.
- `PUT /time/update`: Update an existing time entry.
- `DELETE /time/delete`: Delete a time entry by document ID.
- `GET /time/report`: Get a report of time entries by weeks and category.

### Top Users
- `GET /top_users/getAll`: Get all top users.
- `GET /top_users/top10`: Update the top 10 users leaderboard.
- `GET /top_users/get`: Get top user details by document ID.

## Contributing

Contributions to the Time Tracking Application are welcome. Please follow the standard fork and pull request workflow. If you have any questions or need help with contributions, please open an issue in the repository.