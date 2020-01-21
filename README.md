# Quick App
A technologically agnostic initiator, which bases on user defined templates.

### Use case
In microservices world it's required to create new services rapidly. 
Available tools such as the __[Spring Initializr](https://start.spring.io/)__ sometimes are not suited,
because of network restrictions, company's specific standards and so on.
Possibility to define any template in favourite technology was needed.

### Test it locally
`docker run -d -p 8080:8080 folfix/quick-app`

Application is accessible via __[http://localhost:8080](http://localhost:8080)__.

Without additional configuration Quick App will run using __[this](https://github.com/folfix/quick-app-templates)__ templates.

### How it works
Quick App uses user defined templates stored in Git repository. 
__[Mustache](https://github.com/spullara/mustache.java)__ is used as a template engine.
Quick App defines Manifest as a way of template definition.

### Manifest
Manifest is a file which describes template's variability. It's required, named `quick-app-manifest.json`.
The structure is following:
```$json
{
  "name": "Name of the template",
  "description": "Description of the template",
  "tags": ["any", "tag", "you", "want"],
  "variables": [
    {
      "id": "example-variable-id",
      "name": "Human readable variable name",
      "description": "Variable description"
    }
  ],
  "pathsOverrides": [
    {
      "fromVariable": "example-variable-id",
      "directoriesSeparator": "."
    }
  ],
  "justCopy": [
    "fileToCopy.sh"
  ]
}
```
Manifest's `name`, `description` and `tags` are used to describe template. 

The `variables` represent user defined variables used in template. 
These are directly related with Mustache.
Which means that if you put `{{example-variable-id}}` in template file then it will be fulfilled with input value.

Section called `pathsOverrides` allows to use value from specified `variable` in file/directory paths.
For example: template file called `example-variable-id.html` will be generated as `ftw.html` if user input `ftw` as a `example-variable-id` variable's value. 
It works for directories as well. 
Additionally when `directoriesSeparator` is specified then Quick App will create directory structure based on separator. 
For example: value `my.structure.dir` with `directoriesSeparator` set to `"."` will create structure `my/structure/dir`. 
It's helpful for example in Java packages.

Last section `justCopy` indicates which files are just copied.
Which means that these files are not processed by Mustache.
It's suited for 3rd part scripts like Gradle/Maven wrapper.

### Templates
Every template has it's own directory. As mentioned Manifest file must be present in template directory.
Example of templates Git repository can be found __[here](https://github.com/folfix/quick-app-templates)__.

### Configuration
Quick App is configured by Spring properties prefixed with `quick-app`.
Check __[QuickAppProperties](https://github.com/folfix/quick-app/blob/master/src/main/java/net/folfas/quickapp/QuickAppProperties.java)__ class to see available configuration.

### API
Quick App exposes REST API. Swagger is available at standard url `/swagger-ui.html`.

#### Example Github template private repository
```
quick-app.templates.git-repository.url=https://github.com/$USERNAME/$REPOSITORY.git
quick-app.templates.git-repository.username=$ACCESS_TOKEN
quick-app.templates.git-repository.password=x-oauth-basic
```
You can generate token here: https://github.com/settings/tokens

#### Bitbucket Server template private repository - Personal access token
```
quick-app.templates.git-repository.url=https://$BITBUCKET_SERVER_URL/scm/$PROJECT/$REPOSITORY.git
quick-app.templates.git-repository.bearer-token=$PERSONAL_ACCESS_TOKEN
```

#### Bitbucket Server template private repository - Username and password
```
quick-app.templates.git-repository.url=https://$BITBUCKET_SERVER_URL/scm/$PROJECT/$REPOSITORY.git
quick-app.templates.git-repository.username=$BITBUCKET_USERNAME
quick-app.templates.git-repository.password=$BITBUCKET_PASSWORD
```

### TODO
* describe `ChoiceVariables` in readme
* describe `publish` in readme
* describe `intelliJ` in readme
* tests