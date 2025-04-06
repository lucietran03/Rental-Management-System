# Rental Management System 🏠

Welcome to the Rental Management System, a Java-based application designed to simplify the management of residential and commercial properties, payments, and user roles. This project is built with scalability and maintainability in mind, ensuring long-term usability.

## 🚀 Technology Stack 🛠️

- **Programming Language**: Java ☕
- **UI Framework**: JavaFX 🎨
- **Build Tool**: Maven 🧰
- **Database**: Supabase (PostgreSQL) 🗄️
- **Testing Frameworks**: JUnit5, Mockito ✅

## 🏁 Setup Instructions ⚙️

To set up the Rental Management System on your local machine, follow the steps below:

1. **Ensure you have the Java Development Kit (JDK) installed**. 📥
2. **Clone the repository**: 🖥️
  ```bash
  git clone https://github.com/lucietran03/Rental-Management-System.git
  ```
3. **Build the project using Maven**: 🛠️
  - On macOS/Linux:
    ```bash
    ./mvnw clean install
    ```
  - On Windows:
    ```bash
    mvnw.cmd clean install
    ```
4. **Run the application**: 🚀
  - On macOS/Linux:
    ```bash
    ./mvnw javafx:run
    ```
  - On Windows:
    ```bash
    mvnw.cmd javafx:run
    ```

## ✨ Features

### User Role Management 👥
- **Manager**: 🛡️ Full administrative access to manage accounts, properties, payments, and rental agreements.
- **Host**: 🏘️ Manage properties and handle payments.
- **Owner**: 🏡 Manage properties and view rental agreements.
- **Tenant**: 💳 View properties and manage payments.

### Property Management 🏘️
- 🏠 Create and manage property listings.
- 📸 Upload and manage property images.
- 🔎 View detailed property information.
- 🔄 Transfer property ownership between hosts.
- 🔑 Role-specific property views.

### Rental Agreement System 📜
- 📝 Create and manage rental agreements.
- 📊 Track agreement statuses.
- 📑 Role-based agreement views for Managers, Owners, and others.

### Payment Management 💳
- 💵 Process and track payments.
- 🧾 View payment history by role.
- 🔔 Notify tenants of payment deadlines.
- 🔒 Secure transaction processing.

### Account Management 🔐
- 📝 Update personal information.
- 🔑 Manage login credentials.
- 🎮 Role-specific dashboards and navigation.

## 🗂️ Project Structure

- **Model**: 📋 Defines data entities and validation rules.
- **View**: 🖼️ Manages UI components and user interactions.
- **Controller**: 🎛️ Handles user input and communicates with services.
- **Service**: 🧠 Implements business logic and interacts with the repository layer.
- **Repository**: 🗄️ Manages database queries and data retrieval.

## 🚀 Future Enhancements 🔮

- 💡 Optimize UI components for better performance and responsiveness.
- ⚡ Improve database query efficiency and indexing.
- 🧑‍💻 Implement caching and batch processing for high-demand operations.
- 🧹 Consolidate FXML files to reduce redundancy across user roles.

## 🤝 Authors & Contribution Breakdown

- [Tran Dong Nghi](https://github.com/lucietran03) 🌟 - 25%
- [Tran Tin Dat](https://github.com/TranTinDat22) 🌟 - 25%
- [Nguyen Gia Khang](https://github.com/khangronky) 🌟 - 25%
- [Dan Nguyen Thanh Truc](https://github.com/dnttruc) 🌟 - 25%

## 📚 Resources

### Video Demonstration 🎥
- [Watch on YouTube](https://youtu.be/Gan-7GC8ZT8)

### GitHub Repository 📂
- [Rental Management System](https://github.com/RMIT-Vietnam-Teaching/further-programming-assignment-2-build-a-backend-trustmejunior.git)

## 📜 License

This project is open-source and available under the MIT License. Contributions are welcome to enhance its functionality and sustainability.

## 🙏 Acknowledgments

We extend our gratitude to **Dr. Minh Vu Thanh**, **Dr. Dung Nguyen**, and **Mr. Bao Ho** for their guidance and support throughout the development of this project.
