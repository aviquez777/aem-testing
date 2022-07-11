package com.edc.edcportal.core.errorhandler.configuration;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.AttributeType;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(name = "EDC B2C Configuration", description = "EDC B2C Configuration")
public @interface ErrorHandlerConfigServiceConfiguration {
    @AttributeDefinition(name = "Change Password Entity ID", description = "Entity ID for Change password", type = AttributeType.STRING)
    String getB2CChangePassEntityId();

    @AttributeDefinition(name = "Change Password Status Code", description = "Status Code for Change password", type = AttributeType.STRING)
    String getB2CChangePassStatusCode();

    @AttributeDefinition(name = "Change Password Status Message", description = "Status Message for Change password", type = AttributeType.STRING)
    String getB2CChangePassStatusMessage();

    @AttributeDefinition(name = "Change Password Redirect URL", description = "Change Password Redirect URL", type = AttributeType.STRING)
    String getB2CChangePassRedirectURL();

    @AttributeDefinition(name = "Edit Profile Entity ID", description = "Edit Profile for Edit Profile", type = AttributeType.STRING)
    String getB2CEditProfileEntityId();

    @AttributeDefinition(name = "Edit Profile Status Code", description = "Status Code for Edit Profile", type = AttributeType.STRING)
    String getB2CEditProfileStatusCode();

    @AttributeDefinition(name = "Edit Profile Status Message", description = "Status Message for Edit Profile", type = AttributeType.STRING)
    String getB2CEditProfileStatusMessage();

    @AttributeDefinition(name = "Edit Profile Redirect URL", description = "Edit Profile Redirect URL", type = AttributeType.STRING)
    String getB2CEditProfileRedirectURL();

    @AttributeDefinition(name = "Forget Password Entity ID", description = "Entity ID for ForgetPass", type = AttributeType.STRING)
    String getB2CForgetPassEntityId();

    @AttributeDefinition(name = "Forget Password Status Code", description = "Status Code for ForgetPass", type = AttributeType.STRING)
    String getB2CForgetPassStatusCode();

    @AttributeDefinition(name = "Forget Password Status Message", description = "Status Message for ForgetPass", type = AttributeType.STRING)
    String getB2CForgetPassStatusMessage();

    @AttributeDefinition(name = "Forget Password Redirect URL", description = "Forget Password Redirect URL", type = AttributeType.STRING)
    String getB2CForgetPassRedirectURL();

    @AttributeDefinition(name = "SignUp Entity ID", description = "Entity ID for SignUp", type = AttributeType.STRING)
    String getB2CSignUpEntityId();

    @AttributeDefinition(name = "SignUp Status Code", description = "Status Code for SignUp", type = AttributeType.STRING)
    String getB2CSignUpStatusCode();

    @AttributeDefinition(name = "SignUp Status Message", description = "Status Message for SignUp", type = AttributeType.STRING)
    String getB2CSignUpStatusMessage();

    @AttributeDefinition(name = "SignUp Redirect URL", description = "SignUp Redirect URL", type = AttributeType.STRING)
    String getB2CSignUpRedirectURL();

    @AttributeDefinition(name = "Reset Password Entity ID", description = "Entity ID for Resetpassword", type = AttributeType.STRING)
    String getB2CResetPassEntityId();

    @AttributeDefinition(name = "Reset Password Status Code", description = "Status Code for Resetpassword", type = AttributeType.STRING)
    String getB2CResetPassStatusCode();

    @AttributeDefinition(name = "Reset Password Status Message", description = "Status Message for Resetpassword", type = AttributeType.STRING)
    String getB2CResetPassStatusMessage();

    @AttributeDefinition(name = "Reset Password Redirect URL", description = "Reset Password Redirect URL", type = AttributeType.STRING)
    String getB2CResetPassRedirectURL();

    @AttributeDefinition(name = "SignUp Direct Entity ID", description = "Signup Direct Entity ID", type = AttributeType.STRING)
    String getB2CSignUpDirectEntityId();

    @AttributeDefinition(name = "SignUp Direct Status Code", description = "Signup Direct Status Code", type = AttributeType.STRING)
    String getB2CSignUpDirectStatusCode();

    @AttributeDefinition(name = "SignUp Direct Status Message", description = "Signup Direct Status Message", type = AttributeType.STRING)
    String getB2CSignUpDirectStatusMessage();

    @AttributeDefinition(name = "SignUp Direct Redirect URL", description = "Signup Direct Redirect URL", type = AttributeType.STRING)
    String getB2CSignUpDirectRedirectURL();

    //French
    @AttributeDefinition(name = "French Change Password Entity ID", description = "French Entity ID for Change password", type = AttributeType.STRING)
    String getB2CFRChangePassEntityId();

    @AttributeDefinition(name = "French Change Password Status Code", description = "French Status Code for Change password", type = AttributeType.STRING)
    String getB2CFRChangePassStatusCode();

    @AttributeDefinition(name = "French Change Password Status Message", description = "French Status Message for Change password", type = AttributeType.STRING)
    String getB2CFRChangePassStatusMessage();

    @AttributeDefinition(name = "French Change Password Redirect URL", description = "French Change Password Redirect URL", type = AttributeType.STRING)
    String getB2CFRChangePassRedirectURL();

    @AttributeDefinition(name = "French Edit Profile Entity ID", description = "French Edit Profile for Edit Profile", type = AttributeType.STRING)
    String getB2CFREditProfileEntityId();

    @AttributeDefinition(name = "French Edit Profile Status Code", description = "French Status Code for Edit Profile", type = AttributeType.STRING)
    String getB2CFREditProfileStatusCode();

    @AttributeDefinition(name = "French Edit Profile Status Message", description = "French Status Message for Edit Profile", type = AttributeType.STRING)
    String getB2CFREditProfileStatusMessage();

    @AttributeDefinition(name = "French Edit Profile Redirect URL", description = "French Edit Profile Redirect URL", type = AttributeType.STRING)
    String getB2CFREditProfileRedirectURL();

    @AttributeDefinition(name = "French Forget Password Entity ID", description = "French Entity ID for ForgetPass", type = AttributeType.STRING)
    String getB2CFRForgetPassEntityId();

    @AttributeDefinition(name = "French Forget Password Status Code", description = "French Status Code for ForgetPass", type = AttributeType.STRING)
    String getB2CFRForgetPassStatusCode();

    @AttributeDefinition(name = "French Forget Password Status Message", description = "French Status Message for ForgetPass", type = AttributeType.STRING)
    String getB2CFRForgetPassStatusMessage();

    @AttributeDefinition(name = "French Forget Password Redirect URL", description = "French Forget Password Redirect URL", type = AttributeType.STRING)
    String getB2CFRForgetPassRedirectURL();

    @AttributeDefinition(name = "French SignUp Entity ID", description = "French Entity ID for SignUp", type = AttributeType.STRING)
    String getB2CFRSignUpEntityId();

    @AttributeDefinition(name = "French SignUp Status Code", description = "French Status Code for SignUp", type = AttributeType.STRING)
    String getB2CFRSignUpStatusCode();

    @AttributeDefinition(name = "French SignUp Status Message", description = "French Status Message for SignUp", type = AttributeType.STRING)
    String getB2CFRSignUpStatusMessage();

    @AttributeDefinition(name = "French SignUp Redirect URL", description = "French SignUp Redirect URL", type = AttributeType.STRING)
    String getB2CFRSignUpRedirectURL();

    @AttributeDefinition(name = "French Reset Password Entity ID", description = "Entity ID for Resetpassword", type = AttributeType.STRING)
    String getB2CFRResetPassEntityId();

    @AttributeDefinition(name = "French Reset Password Status Code", description = "Status Code for Resetpassword", type = AttributeType.STRING)
    String getB2CFRResetPassStatusCode();

    @AttributeDefinition(name = "French Reset Password Status Message", description = "Status Message for Resetpassword", type = AttributeType.STRING)
    String getB2CFRResetPassStatusMessage();

    @AttributeDefinition(name = "French Reset Password Redirect URL", description = "Reset Password Redirect URL", type = AttributeType.STRING)
    String getB2CFRResetPassRedirectURL();

    @AttributeDefinition(name = "French SignUp Direct Entity ID", description = "French Signup Direct Entity ID", type = AttributeType.STRING)
    String getB2CFRSignUpDirectEntityId();

    @AttributeDefinition(name = "French SignUp Direct Status Code", description = "French Signup Direct Status Code", type = AttributeType.STRING)
    String getB2CFRSignUpDirectStatusCode();

    @AttributeDefinition(name = "French SignUp Direct Status Message", description = "French Signup Direct Status Message", type = AttributeType.STRING)
    String getB2CFRSignUpDirectStatusMessage();

    @AttributeDefinition(name = "French SignUp Direct Redirect URL", description = "French Signup Direct Redirect URL", type = AttributeType.STRING)
    String getB2CFRSignUpDirectRedirectURL();

}