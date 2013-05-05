[#ftl]
[#-- @ftlvariable name="unsubscribeUrl" type="java.lang.String" --]
[#-- @ftlvariable name="urlRoot" type="java.lang.String" --]
[#-- @ftlvariable name="shopper" type="com.stylebeeapp.appengine.domain.ShopperEntity" --]
[#-- @ftlvariable name="token" type="java.lang.String" --]
[#-- @ftlvariable name="fromName" type="java.lang.String" --]
[#-- @ftlvariable name="fromEmail" type="java.lang.String" --]
				</td>
			</tr>
			<tr>
				<td style="padding: 0; margin: 0; vertical-align: top; text-align: center; font:  11px/16px  Arial, Sans-serif;" colspan="2">
                    Tento email jste obdržel(a), protože jste zaregistrován v aplikaci Koordinátor jako dobrovolník.<br/>
					[#if unsubscribeUrl?? ]
						<strong>Odhlášení:</strong> Žádné další emaily mi <a href="${unsubscribeUrl}">prosím nezasílejte</a>.
					[/#if]
				</td>
			</tr>
		</table>
	</body>
</html>