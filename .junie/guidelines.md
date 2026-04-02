# Guidelines for Junie

**Important:** Strictly follow the instructions below in sequence:

---md

###  Before Starting Work

- **Always check for a clean working directory before starting.** Use stashing if needed. Name the stash `{timestamp}-{short-description}`.
- **Write down the timestamp** of the session start.
  Format: `yyyy-MM-dd_hh-mm`

---

###  Planning Phase

- **Generate a `plan-{timestamp}.md` file** before writing any code.
- **Use the plan file to generate a detailed, enumerated task list.**
- **Store the task list in `tasks-{timestamp}.md`.**
- **Ensure each task is atomic** (can be completed independently).

---

###  Execution Phase

- **Do the work as defined by the user prompt and the task list.**
- **After each task is completed, mark it as done** in the `tasks-{timestamp}.md` file.
- **Log any deviations** from the plan (with justification) in `notes-{timestamp}.md`.
- **When generating code, include comments** with:
	- Junie signature
	- Date
	- Purpose of the code
- **Always follow project-specific code style** and naming conventions.
- **Run formatting and linting tools** automatically after code generation.
- **Include or update unit tests** for each new feature or bug fix.
- **Run existing test suites before and after** making changes to ensure stability.
- **Update documentation** (README, internal wikis, etc.) if functionality or APIs are affected.
- save your plan, tasks,and notes documents in a folder called `junie-files-{timestamp}` in the `.junie` folder in the root of the project.

---
### Testing
- Use JUnit 5 for all unit tests.
- Use unit-vintage-engine to run JUnit 4 tests if necessary.
- Place test classes in the `src/test/java` directory.


## Code Style
- Follow the Google Java Style Guide.
- Use 4 spaces for indentation.
- Limit line length to 100 characters.
- Use lombok annotations where appropriate (e.g.,  `@Data` and `@Getter`, `@Setter` for entities).
- for logging use `@Slf4j` from lombok. doen't use system.out.println. but use the logger.
- for external rest calls use the new spring service interface @HttpExchange, @GetExhange, etc annotation. and use a RestClient to call the service.
- create utils classes for common functionality (e.g., string manipulation, date formatting).
- use lambda expressions where appropriate to improve readability.
- give lambda expressions meaningful names.
- use new switch expressions for better readability.
- use small functions that do one thing well.
- for external properties create a property class with `@ConfigurationProperties` annotation, and set it up in a @Config, and locate it in the config folder.
- use jackson annotations for JSON serialization/deserialization (e.g., `@JsonProperty`, `@JsonIgnore`).
- user jackson for copy objects.
- use code chaining, and configure it in the lombok config file if necessary.
- keep functions small and focused on a single task.
- For the properties use a prefix `haarlem.{projectname}` to avoid conflicts with other projects.
- For for packages `nl.haarlem.{package-name}.*`.
- Use Java 21 version in the pom file.


## Dependencies
- Prefer using Maven for dependency management.
- Avoid introducing new dependencies without approval.
- Use the latest stable versions of dependencies.
- Use Lombok for boilerplate code reduction (e.g., getters, setters, builders, constructors).
- when creating new project or module use maven as the build tool. Prefer Spring boot depencies for new projects or modules.

## Database
- Store database configurations in `src/main/resources/application.properties`.
- name the database to the projectname. when there is a database with the same name then {projectname}-{timestamp}

## Security
- Sanitize all user inputs to prevent SQL injection.
- Implement authentication using Spring Security.

---

### Completion

- **Before final commit**, re-verify that:
	- All tasks are marked as completed in `tasks-{timestamp}.md`
	- All relevant files (plan, tasks, notes) contain the correct timestamp
	- All generated and modified files are added and committed
- **Commit the work with a concise summary** of the changes in the commit message.

---

### Final Step

- **Automatically open a pull request** upon pushing the branch to remote.




## Prompt for new user login EXAMPLE

I want to implement a new login feature.

Before you write any code:
- Create a new git branch: feature/login-ui
- Timestamp: 2025-04-23_10-45
- Create plan-2025-04-23_10-45.md outlining your approach
- Generate tasks-2025-04-23_10-45.md from the plan (each task should be atomic)
- Then wait for me to confirm before starting implementation.
