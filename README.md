This project was refactored in September 2025 to work with Tomcat 10. 
During this refactoring, the codebase was migrated from using javax.ws to jakarta.ws. 
All modules that are not used by Artifactory were deleted to avoid unnecessary work.
`org.eclipse.persistence` jpa was removed from the project as well.
