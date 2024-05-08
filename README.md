# BeautyBard

### Installation/Running

#### Client

(One time) To run the frontend dev server, you'll need to install the node dependencies in the `app` module.
```sh
$ cd modules/app
$ npm i
```

Once the dependencies are installed, in a terminal window, run
```sh
$ sbt '~app/fastLinkJS'
```
to start the incremental compiler. This will listen for changes to source files
in the `app` and `common` modules, and re-build the ES/JS module for the vite project.

In another terminal window, run
```sh
$ npm run dev
```
to start the vite dev server, which will listen for changes to the files generated from
the sbt task and automatically reload the page as updates are made.

#### Server

(One time) To run the backend/api server, you'll need to create an `application.conf` file under
`src/main/resources` in the `server` module. See `application.conf.example` to see what needs to be set.

To set up a Postgres/CockroachDB instance with the right tables, you can use the script in `sql/init.sql`.

Once you have the config file set up, run
```sh
$ sbt 'server/runMain co.beautyBard.Main'
```
to start the server.
