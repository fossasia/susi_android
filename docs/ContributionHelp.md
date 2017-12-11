## How to Contribute

### Raising an issue:
 This is an Open Source project and we would be happy to see contributors who report bugs and file feature requests submitting pull requests as well.
 This project adheres to the Contributor Covenant code of conduct.
 By participating, you are expected to uphold this code style.
 Please report issues here [Issues - fossasia/susi_android](https://github.com/fossasia/susi_android/issues)

### Branch Policy

#### Sending pull requests:

Say you want to contribute changes to someone else’s repository (eg, this one).

It is advisable to clone only the development branch using following command:
Go to the repository on github. (Say it’s by myfriend, and is called the_repo, then you’ll find it at http://github.com/myfriend/the_repo.)

`git clone -b <branch> <remote_repo>`

Click the “Fork” button at the top right.

You’ll now have your own copy of that repository in your github account.

**Example:**

`git clone -b my-branch git@github.com:user/myproject.git`

Open a terminal/shell.

Type

`$ git clone git@github.com:username/the_repo`

where username is your username.

You’ll now have a local copy of your version of that repository.

*For further details on how to contribute as a first time contributer visit [Contribute to someone's repository](http://kbroman.org/github_tutorial/pages/fork.html)*

#### Change into that project directory (the_repo):

`$ cd the_repo`

#### Add a connection to the original owner’s repository.

`$ git remote add myfriend git://github.com/myfriend/the_repo`

Note the distinction between git@github.com: in the first case and git://github.com/ in the second case. I’m not sure why these need to be the way they are, but that’s what works for me.

Also note the first myfriend does not need to be the same as the username of myfriend. You could very well choose:

`$ git remote add repo_nickname git://github.com/myfriend/the_repo`

#### To check this remote add set up:

`$ git remote -v`

#### Make changes to files.

`git add` and `git commit` those changes

`git push` them back to github. These will go to your version of the repository.

Note: if you get an error like:

error: src refspec master does not match any.
error: failed to push some refs to 'git@github.com:username/the_repo'
Then try `git push origin HEAD:gh-pages` (see stackoverflow.). Typing `git show-ref` can show what reference to put after HEAD.

#### Now Create a PR
Go to your version of the repository on github.

Click the “Pull Request” button at the top.

Note that your friend’s repository will be on the left and your repository will be on the right.

Click the green button “Create pull request”. Give a succinct and informative title, in the comment field give a short explanation of the changes and click the green button “Create pull request” again.

#### Pulling others’ changes
Before you make further changes to the repository, you should check that your version is up to date relative to your friend’s version.

Go into the directory for the project and type:

`$ git pull myfriend master`

This will pull down and merge all of the changes that your friend has made.

Now push them back to your github repository.

`$ git push`
